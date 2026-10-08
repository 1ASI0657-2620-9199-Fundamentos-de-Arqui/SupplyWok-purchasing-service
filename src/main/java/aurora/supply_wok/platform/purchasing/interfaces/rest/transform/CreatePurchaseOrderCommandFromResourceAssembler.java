package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.CreatePurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderItemResource;

/**
 * Assembler to convert a CreatePurchaseOrderResource to a CreatePurchaseOrderCommand.
 */
public class CreatePurchaseOrderCommandFromResourceAssembler {

    public static CreatePurchaseOrderCommand toCommandFromResource(CreatePurchaseOrderResource resource) {
        return new CreatePurchaseOrderCommand(
                resource.code(),
                resource.supplierId(),
                resource.supplierName(),
                resource.restaurantName(),
                resource.orderDate(),
                resource.estimatedDate(),
                resource.priority(),
                resource.status(),
                resource.items().stream().map(CreatePurchaseOrderCommandFromResourceAssembler::toItemCommand).toList()
        );
    }

    private static CreatePurchaseOrderItemCommand toItemCommand(PurchaseOrderItemResource resource) {
        return new CreatePurchaseOrderItemCommand(
                null,
                resource.inventoryItemId(),
                resource.productName(),
                resource.quantity(),
                resource.unitPrice(),
                resource.unitType()
        );
    }
}
