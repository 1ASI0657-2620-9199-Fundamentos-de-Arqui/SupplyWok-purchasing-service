package aurora.supply_wok.platform.suppliers.application.queryservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllClientsBySupplierIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service for supplier clients.
 */
public interface ClientQueryService {
    Optional<List<Client>> handle(GetAllClientsBySupplierIdQuery query);
}
