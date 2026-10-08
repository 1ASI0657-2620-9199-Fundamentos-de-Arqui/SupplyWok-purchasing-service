package aurora.supply_wok.platform.purchasing.application.queryservices;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetAllPurchaseOrdersQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersBySupplierIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling purchase order queries.
 */
public interface PurchaseOrderQueryService {
    List<PurchaseOrder> handle(GetAllPurchaseOrdersQuery query);

    List<PurchaseOrder> handle(GetPurchaseOrdersBySupplierIdQuery query);

    Optional<PurchaseOrder> handle(GetPurchaseOrderByIdQuery query);
}
