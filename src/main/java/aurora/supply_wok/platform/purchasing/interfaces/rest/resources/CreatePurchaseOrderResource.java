package aurora.supply_wok.platform.purchasing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource for creating a purchase order.
 */
@Schema(
        name = "CreatePurchaseOrderRequest",
        description = "Request payload for creating a purchase order",
        example = """
                {"code": "PO-24021", "supplierId": 201, "supplierName": "Golden Wok Produce", "restaurantName": "Gran Dragon Chifa", "orderDate": "2026-05-10", "estimatedDate": "2026-05-11", "priority": "High", "status": "Pending", "items": [{"inventoryItemId": 101, "productName": "Rice", "quantity": 25.00, "unitPrice": 4.50, "unitType": "kg"}]}
                """
)
public record CreatePurchaseOrderResource(
        @NotBlank
        @Schema(description = "Purchase order code", example = "PO-24021", minLength = 1, maxLength = 20)
        String code,

        @NotNull
        @Schema(description = "Supplier unique identifier", example = "201")
        Long supplierId,

        @Schema(description = "Supplier display name kept for API compatibility", example = "Golden Wok Produce")
        String supplierName,

        @NotBlank
        @Schema(description = "Restaurant display name", example = "Gran Dragon Chifa", minLength = 1, maxLength = 100)
        String restaurantName,

        @NotNull
        @Schema(description = "Order date", example = "2026-05-10", type = "string", format = "date")
        LocalDate orderDate,

        @Schema(description = "Estimated delivery date", example = "2026-05-11", type = "string", format = "date")
        LocalDate estimatedDate,

        @NotBlank
        @Schema(description = "Purchase order priority", example = "High", allowableValues = {"High", "Medium", "Low"})
        String priority,

        @Schema(description = "Requested initial status; kept for API compatibility", example = "Pending", allowableValues = {"Pending"})
        String status,

        @NotEmpty
        @Valid
        @Schema(description = "Purchase order line items")
        List<PurchaseOrderItemResource> items
) {
}
