package aurora.supply_wok.platform.suppliers.application.queryservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllSuppliersQuery;

import java.util.List;

/**
 * Query service for supplier read operations.
 */
public interface SupplierQueryService {
    List<Supplier> handle(GetAllSuppliersQuery query);
}
