package aurora.supply_wok.platform.purchasing.domain.model.commands;

/**
 * Command to delete a purchase order.
 */
public record DeletePurchaseOrderCommand(
        Long id
) {
}
