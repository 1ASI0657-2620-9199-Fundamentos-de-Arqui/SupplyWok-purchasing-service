package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CatalogItemResource;

/**
 * Assembler to convert a CatalogItem entity to a CatalogItemResource.
 */
public class CatalogItemResourceFromEntityAssembler {

    public static CatalogItemResource toResourceFromEntity(CatalogItem entity) {
        return new CatalogItemResource(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getPrice(),
                entity.getUnit().name(),
                entity.getDeliveryConditions()
        );
    }
}
