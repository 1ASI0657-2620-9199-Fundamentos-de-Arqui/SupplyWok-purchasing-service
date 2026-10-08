package aurora.supply_wok.platform.suppliers.application.commandservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.domain.model.commands.LinkClientToSupplierCommand;

import java.util.Optional;

/**
 * Command service for supplier-client relationship operations.
 */
public interface SupplierClientCommandService {
    Optional<Client> handle(LinkClientToSupplierCommand command);
}
