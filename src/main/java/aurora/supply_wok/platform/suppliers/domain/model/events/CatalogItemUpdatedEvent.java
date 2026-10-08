package aurora.supply_wok.platform.suppliers.domain.model.events;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;

import java.math.BigDecimal;

public record CatalogItemUpdatedEvent(
        Long catalogItemId,
        Long supplierId,
        String name,
        String category,
        BigDecimal price
) {
    public static CatalogItemUpdatedEvent from(CatalogItem catalogItem) {
        return new CatalogItemUpdatedEvent(
                catalogItem.getId(),
                catalogItem.getSupplierId(),
                catalogItem.getName(),
                catalogItem.getCategory(),
                catalogItem.getPrice()
        );
    }
}
