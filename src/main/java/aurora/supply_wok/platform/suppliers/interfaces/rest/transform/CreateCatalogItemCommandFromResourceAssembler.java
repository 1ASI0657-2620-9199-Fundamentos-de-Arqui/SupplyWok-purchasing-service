package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CreateCatalogItemResource;

/**
 * Assembler to convert a CreateCatalogItemResource to a CreateCatalogItemCommand.
 */
public class CreateCatalogItemCommandFromResourceAssembler {

    public static CreateCatalogItemCommand toCommandFromResource(Long supplierId, CreateCatalogItemResource resource) {
        ECatalogUnit catalogUnit;
        try {
            catalogUnit = ECatalogUnit.valueOf(resource.unit());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid catalog unit: " + resource.unit());
        }

        return new CreateCatalogItemCommand(
                supplierId,
                resource.name(),
                resource.category(),
                resource.price(),
                catalogUnit,
                resource.deliveryConditions()
        );
    }
}
