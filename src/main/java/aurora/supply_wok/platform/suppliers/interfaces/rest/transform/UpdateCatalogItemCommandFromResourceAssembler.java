package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.commands.UpdateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.UpdateCatalogItemResource;

/**
 * Assembler to convert an UpdateCatalogItemResource to an UpdateCatalogItemCommand.
 */
public class UpdateCatalogItemCommandFromResourceAssembler {

    public static UpdateCatalogItemCommand toCommandFromResource(Long supplierId, Long catalogItemId, UpdateCatalogItemResource resource) {
        ECatalogUnit catalogUnit;
        try {
            catalogUnit = ECatalogUnit.valueOf(resource.unit());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid catalog unit: " + resource.unit());
        }

        return new UpdateCatalogItemCommand(
                supplierId,
                catalogItemId,
                resource.name(),
                resource.category(),
                resource.price(),
                catalogUnit,
                resource.deliveryConditions()
        );
    }
}
