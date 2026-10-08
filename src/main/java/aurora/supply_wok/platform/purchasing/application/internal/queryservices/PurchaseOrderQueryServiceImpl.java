package aurora.supply_wok.platform.purchasing.application.internal.queryservices;

import aurora.supply_wok.platform.purchasing.application.queryservices.PurchaseOrderQueryService;
import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetAllPurchaseOrdersQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdAndRestaurantIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdAndSupplierIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersByRestaurantIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersBySupplierIdQuery;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the PurchaseOrderQueryService interface.
 */
@Service
public class PurchaseOrderQueryServiceImpl implements PurchaseOrderQueryService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderQueryServiceImpl(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public List<PurchaseOrder> handle(GetAllPurchaseOrdersQuery query) {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public List<PurchaseOrder> handle(GetPurchaseOrdersByRestaurantIdQuery query) {
        return purchaseOrderRepository.findAllByRestaurantId(query.restaurantId());
    }

    @Override
    public List<PurchaseOrder> handle(GetPurchaseOrdersBySupplierIdQuery query) {
        return purchaseOrderRepository.findAllBySupplierId(query.supplierId());
    }

    @Override
    public Optional<PurchaseOrder> handle(GetPurchaseOrderByIdQuery query) {
        return purchaseOrderRepository.findById(query.purchaseOrderId());
    }

    @Override
    public Optional<PurchaseOrder> handle(GetPurchaseOrderByIdAndRestaurantIdQuery query) {
        return purchaseOrderRepository.findByIdAndRestaurantId(query.purchaseOrderId(), query.restaurantId());
    }

    @Override
    public Optional<PurchaseOrder> handle(GetPurchaseOrderByIdAndSupplierIdQuery query) {
        return purchaseOrderRepository.findByIdAndSupplierId(query.purchaseOrderId(), query.supplierId());
    }
}
