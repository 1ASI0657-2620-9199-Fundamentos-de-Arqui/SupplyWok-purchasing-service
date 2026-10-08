package aurora.supply_wok.platform.suppliers.interfaces.acl;

import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierClientCommandService;
import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierCommandService;
import aurora.supply_wok.platform.suppliers.application.queryservices.SupplierQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.commands.LinkClientToSupplierCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpsertSupplierFromProfileCommand;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllSuppliersQuery;
import aurora.supply_wok.platform.suppliers.interfaces.acl.resources.SupplierIdentityAclResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ACL facade that exposes supplier identity data to other bounded contexts.
 */
@Service
public class SuppliersContextFacade {

    private final SupplierQueryService supplierQueryService;
    private final SupplierCommandService supplierCommandService;
    private final SupplierClientCommandService supplierClientCommandService;

    public SuppliersContextFacade(SupplierQueryService supplierQueryService,
                                  SupplierCommandService supplierCommandService,
                                  SupplierClientCommandService supplierClientCommandService) {
        this.supplierQueryService = supplierQueryService;
        this.supplierCommandService = supplierCommandService;
        this.supplierClientCommandService = supplierClientCommandService;
    }

    public Optional<SupplierIdentityAclResource> getSupplierIdentityById(Long supplierId) {
        if (supplierId == null || supplierId <= 0) {
            return Optional.empty();
        }

        return supplierQueryService.handle(new GetAllSuppliersQuery()).stream()
                .filter(supplier -> supplierId.equals(supplier.getId()))
                .findFirst()
                .map(supplier -> new SupplierIdentityAclResource(supplier.getId(), supplier.getName()));
    }

    public void ensureSupplierClientLinked(Long supplierId, String clientName) {
        supplierClientCommandService.handle(new LinkClientToSupplierCommand(supplierId, clientName));
    }

    public void syncSupplierProfile(String name, String contactName, String email, String phone) {
        supplierCommandService.handle(new UpsertSupplierFromProfileCommand(
                name,
                contactName,
                email,
                phone,
                "General",
                "24h",
                "2h"
        ));
    }
}
