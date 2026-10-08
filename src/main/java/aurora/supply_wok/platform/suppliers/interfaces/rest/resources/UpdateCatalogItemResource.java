package aurora.supply_wok.platform.suppliers.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Resource for updating a catalog item.
 */
@Schema(
        name = "UpdateCatalogItemRequest",
        description = "Request payload for updating a supplier catalog item",
        example = """
                {"name": "Cebolla china premium", "category": "Vegetables", "price": 3.20, "unit": "KG", "deliveryConditions": "Next-day before 11:00"}
                """
)
public record UpdateCatalogItemResource(
        @NotBlank
        @Schema(description = "Catalog item name", example = "Cebolla china premium", minLength = 1, maxLength = 100)
        String name,

        @NotBlank
        @Schema(description = "Catalog item category", example = "Vegetables", minLength = 1, maxLength = 80)
        String category,

        @NotNull
        @DecimalMin(value = "0.01")
        @Schema(description = "Catalog item price", example = "3.20")
        BigDecimal price,

        @NotBlank
        @Schema(description = "Catalog item unit", example = "KG", allowableValues = {"KG", "LTR", "BOX"})
        String unit,

        @NotBlank
        @Schema(description = "Delivery conditions", example = "Next-day before 11:00", minLength = 1, maxLength = 250)
        String deliveryConditions
) {
}
