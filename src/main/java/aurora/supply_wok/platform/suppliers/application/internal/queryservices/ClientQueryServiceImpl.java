package aurora.supply_wok.platform.suppliers.application.internal.queryservices;

import aurora.supply_wok.platform.suppliers.application.queryservices.ClientQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllClientsBySupplierIdQuery;
import aurora.supply_wok.platform.suppliers.domain.repositories.ClientRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Query service implementation for supplier clients.
 */
@Service
public class ClientQueryServiceImpl implements ClientQueryService {

    private final ClientRepository clientRepository;
    private final SupplierRepository supplierRepository;

    public ClientQueryServiceImpl(ClientRepository clientRepository, SupplierRepository supplierRepository) {
        this.clientRepository = clientRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Optional<List<Client>> handle(GetAllClientsBySupplierIdQuery query) {
        if (!supplierRepository.existsById(query.supplierId())) {
            return Optional.empty();
        }

        return Optional.of(clientRepository.findAllBySupplierId(query.supplierId()));
    }
}
