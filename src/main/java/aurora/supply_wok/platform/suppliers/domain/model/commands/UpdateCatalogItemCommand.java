package aurora.supply_wok.platform.suppliers.domain.model.commands;

import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;

import java.math.BigDecimal;

/**
 * Command to update a catalog item.
 */
public record UpdateCatalogItemCommand(
        Long supplierId,
        Long catalogItemId,
        String name,
        String category,
        BigDecimal price,
        ECatalogUnit unit,
        String deliveryConditions
) {
}
