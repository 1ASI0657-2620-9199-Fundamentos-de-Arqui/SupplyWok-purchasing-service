package aurora.supply_wok.platform.purchasing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating the purchase order status.
 */
@Schema(
        name = "UpdatePurchaseOrderStatusRequest",
        description = "Request payload for updating only the purchase order status",
        example = """
                {"status": "In Transit"}
                """
)
public record UpdatePurchaseOrderStatusResource(
        @NotBlank
        @Schema(description = "Requested purchase order status", example = "In Transit",
                allowableValues = {"Pending", "Confirmed", "In Transit", "Delivered", "Delayed"})
        String status
) {
}
