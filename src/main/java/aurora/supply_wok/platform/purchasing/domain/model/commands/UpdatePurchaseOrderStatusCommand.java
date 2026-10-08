package aurora.supply_wok.platform.purchasing.domain.model.commands;

/**
 * Command to update only the purchase order status.
 */
public record UpdatePurchaseOrderStatusCommand(
        Long id,
        String status,
        Long restaurantId,
        Long supplierId
) {
    public UpdatePurchaseOrderStatusCommand(Long id, String status) {
        this(id, status, null, null);
    }
}
