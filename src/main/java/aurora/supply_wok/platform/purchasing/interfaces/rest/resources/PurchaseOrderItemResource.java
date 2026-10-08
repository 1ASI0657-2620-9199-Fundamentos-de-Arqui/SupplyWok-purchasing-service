package aurora.supply_wok.platform.purchasing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Purchase order line item resource.
 */
@Schema(
        name = "PurchaseOrderItemResource",
        description = "Purchase order line item payload",
        example = """
                {"id": 1, "inventoryItemId": 101, "productName": "Rice", "quantity": 25.00, "unitPrice": 4.50, "unitType": "kg"}
                """
)
public record PurchaseOrderItemResource(
        @Schema(description = "Line item unique identifier", example = "1")
        Long id,

        @Schema(description = "Optional inventory item identifier", example = "101")
        Long inventoryItemId,

        @NotBlank
        @Schema(description = "Purchased product name", example = "Rice", minLength = 1, maxLength = 100)
        String productName,

        @NotNull
        @DecimalMin(value = "0.01")
        @Schema(description = "Purchased quantity", example = "25.00")
        BigDecimal quantity,

        @NotNull
        @DecimalMin(value = "0.01")
        @Schema(description = "Unit price", example = "4.50")
        BigDecimal unitPrice,

        @NotBlank
        @Schema(description = "Unit type", example = "kg", minLength = 1, maxLength = 20)
        String unitType
) {
}
