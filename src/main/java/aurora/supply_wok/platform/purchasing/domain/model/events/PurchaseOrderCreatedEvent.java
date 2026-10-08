package aurora.supply_wok.platform.purchasing.domain.model.events;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;

/**
 * Domain event raised when a purchase order is created.
 */
public record PurchaseOrderCreatedEvent(
        Long purchaseOrderId,
        String code,
        Long supplierId,
        String supplierName,
        String restaurantName,
        String priority,
        String status
) {
    public static PurchaseOrderCreatedEvent from(PurchaseOrder purchaseOrder) {
        return new PurchaseOrderCreatedEvent(
                purchaseOrder.getId(),
                purchaseOrder.getCode(),
                purchaseOrder.getSupplierId(),
                purchaseOrder.getSupplierName(),
                purchaseOrder.getRestaurantName(),
                purchaseOrder.getPriority().name(),
                purchaseOrder.getStatus().name()
        );
    }
}
