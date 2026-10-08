package aurora.supply_wok.platform.suppliers.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Catalog item response resource.
 */
@Schema(
        name = "CatalogItemResponse",
        description = "Catalog item information response",
        example = """
                {"id": 1, "name": "Cebolla china", "category": "Vegetables", "price": 2.90, "unit": "KG", "deliveryConditions": "Next-day before 11:00"}
                """
)
public record CatalogItemResource(
        @Schema(description = "Catalog item unique identifier", example = "1")
        Long id,

        @Schema(description = "Catalog item display name", example = "Cebolla china")
        String name,

        @Schema(description = "Catalog item category", example = "Vegetables")
        String category,

        @Schema(description = "Catalog item price", example = "2.90")
        BigDecimal price,

        @Schema(description = "Catalog item unit", example = "KG", allowableValues = {"KG", "LTR", "BOX"})
        String unit,

        @Schema(description = "Delivery conditions for the item", example = "Next-day before 11:00")
        String deliveryConditions
) {
}
