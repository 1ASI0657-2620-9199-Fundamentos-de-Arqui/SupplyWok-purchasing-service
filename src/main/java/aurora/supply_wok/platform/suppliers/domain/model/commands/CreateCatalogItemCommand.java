package aurora.supply_wok.platform.suppliers.domain.model.commands;

import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;

import java.math.BigDecimal;

/**
 * Command to create a catalog item.
 */
public record CreateCatalogItemCommand(
        Long supplierId,
        String name,
        String category,
        BigDecimal price,
        ECatalogUnit unit,
        String deliveryConditions
) {
}
