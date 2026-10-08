package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderItemResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderResource;

/**
 * Assembler to convert an UpdatePurchaseOrderResource to an UpdatePurchaseOrderCommand.
 */
public class UpdatePurchaseOrderCommandFromResourceAssembler {

    public static UpdatePurchaseOrderCommand toCommandFromResource(Long purchaseOrderId, UpdatePurchaseOrderResource resource) {
        return new UpdatePurchaseOrderCommand(
                purchaseOrderId,
                resource.code(),
                resource.supplierId(),
                resource.supplierName(),
                resource.restaurantName(),
                resource.orderDate(),
                resource.estimatedDate(),
                resource.priority(),
                resource.status(),
                resource.items().stream().map(UpdatePurchaseOrderCommandFromResourceAssembler::toItemCommand).toList()
        );
    }

    private static CreatePurchaseOrderItemCommand toItemCommand(PurchaseOrderItemResource resource) {
        return new CreatePurchaseOrderItemCommand(
                resource.id(),
                resource.inventoryItemId(),
                resource.productName(),
                resource.quantity(),
                resource.unitPrice(),
                resource.unitType()
        );
    }
}
