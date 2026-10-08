package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderStatusCommand;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderStatusResource;

/**
 * Assembler to convert an UpdatePurchaseOrderStatusResource to an UpdatePurchaseOrderStatusCommand.
 */
public class UpdatePurchaseOrderStatusCommandFromResourceAssembler {

    public static UpdatePurchaseOrderStatusCommand toCommandFromResource(Long purchaseOrderId, UpdatePurchaseOrderStatusResource resource) {
        return new UpdatePurchaseOrderStatusCommand(purchaseOrderId, resource.status());
    }
}
