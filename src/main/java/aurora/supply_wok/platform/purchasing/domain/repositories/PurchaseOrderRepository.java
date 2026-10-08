package aurora.supply_wok.platform.purchasing.domain.repositories;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;

import java.util.List;
import java.util.Optional;

/**
 * Purchase order repository port.
 */
public interface PurchaseOrderRepository {
    List<PurchaseOrder> findAll();

    List<PurchaseOrder> findAllBySupplierId(Long supplierId);

    Optional<PurchaseOrder> findById(Long purchaseOrderId);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long excludedId);

    PurchaseOrder save(PurchaseOrder purchaseOrder);

    void delete(PurchaseOrder purchaseOrder);
}
