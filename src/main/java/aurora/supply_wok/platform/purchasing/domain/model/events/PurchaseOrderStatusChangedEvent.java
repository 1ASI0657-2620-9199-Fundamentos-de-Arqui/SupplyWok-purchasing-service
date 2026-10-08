package aurora.supply_wok.platform.purchasing.domain.model.events;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;

/**
 * Domain event raised when a purchase order status changes.
 */
public record PurchaseOrderStatusChangedEvent(
        Long purchaseOrderId,
        String code,
        Long supplierId,
        String supplierName,
        String restaurantName,
        EPurchaseOrderStatus previousStatus,
        EPurchaseOrderStatus currentStatus
) {
    public static PurchaseOrderStatusChangedEvent from(PurchaseOrder purchaseOrder, EPurchaseOrderStatus previousStatus) {
        return new PurchaseOrderStatusChangedEvent(
                purchaseOrder.getId(),
                purchaseOrder.getCode(),
                purchaseOrder.getSupplierId(),
                purchaseOrder.getSupplierName(),
                purchaseOrder.getRestaurantName(),
                previousStatus,
                purchaseOrder.getStatus()
        );
    }
}
