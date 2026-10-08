package aurora.supply_wok.platform.suppliers.application.internal.queryservices;

import aurora.supply_wok.platform.suppliers.application.queryservices.SupplierQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllSuppliersQuery;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default supplier query service implementation.
 */
@Service
public class SupplierQueryServiceImpl implements SupplierQueryService {

    private final SupplierRepository supplierRepository;

    public SupplierQueryServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> handle(GetAllSuppliersQuery query) {
        return supplierRepository.findAll();
    }
}
