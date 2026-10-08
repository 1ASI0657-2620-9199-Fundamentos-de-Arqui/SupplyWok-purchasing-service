package aurora.supply_wok.platform.purchasing.application.commandservices;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.DeletePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderStatusCommand;

import java.util.Optional;

/**
 * Service interface for handling purchase order commands.
 */
public interface PurchaseOrderCommandService {
    Optional<PurchaseOrder> handle(CreatePurchaseOrderCommand command);

    Optional<PurchaseOrder> handle(UpdatePurchaseOrderCommand command);

    Optional<PurchaseOrder> handle(UpdatePurchaseOrderStatusCommand command);

    boolean handle(DeletePurchaseOrderCommand command);
}
