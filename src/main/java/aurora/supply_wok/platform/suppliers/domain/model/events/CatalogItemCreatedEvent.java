package aurora.supply_wok.platform.suppliers.domain.model.events;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;

import java.math.BigDecimal;

public record CatalogItemCreatedEvent(
        Long catalogItemId,
        Long supplierId,
        String name,
        String category,
        BigDecimal price
) {
    public static CatalogItemCreatedEvent from(CatalogItem catalogItem) {
        return new CatalogItemCreatedEvent(
                catalogItem.getId(),
                catalogItem.getSupplierId(),
                catalogItem.getName(),
                catalogItem.getCategory(),
                catalogItem.getPrice()
        );
    }
}
