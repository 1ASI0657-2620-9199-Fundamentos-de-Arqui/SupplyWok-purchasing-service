package aurora.supply_wok.platform.purchasing.domain.repositories;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;

import java.util.List;
import java.util.Optional;

/**
 * Purchase order repository port.
 */
public interface PurchaseOrderRepository {
    List<PurchaseOrder> findAll();

    List<PurchaseOrder> findAllByRestaurantId(Long restaurantId);

    List<PurchaseOrder> findAllBySupplierId(Long supplierId);

    Optional<PurchaseOrder> findById(Long purchaseOrderId);

    Optional<PurchaseOrder> findByIdAndRestaurantId(Long purchaseOrderId, Long restaurantId);

    Optional<PurchaseOrder> findByIdAndSupplierId(Long purchaseOrderId, Long supplierId);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long excludedId);

    PurchaseOrder save(PurchaseOrder purchaseOrder);

    void delete(PurchaseOrder purchaseOrder);
}
