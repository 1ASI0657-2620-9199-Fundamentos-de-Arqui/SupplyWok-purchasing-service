package aurora.supply_wok.platform.suppliers.application.commandservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.DeleteCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpdateCatalogItemCommand;

import java.util.Optional;

/**
 * Service interface for handling supplier catalog commands.
 */
public interface CatalogItemCommandService {
    Optional<Long> handle(CreateCatalogItemCommand command);

    Optional<CatalogItem> handle(UpdateCatalogItemCommand command);

    boolean handle(DeleteCatalogItemCommand command);
}
