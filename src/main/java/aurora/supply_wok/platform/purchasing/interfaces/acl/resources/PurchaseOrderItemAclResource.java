package aurora.supply_wok.platform.purchasing.interfaces.acl.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Purchase order line item payload exposed through the Purchasing ACL.
 */
@Schema(name = "PurchaseOrderItemAclResource", description = "Purchase order line item exposed through the Purchasing ACL")
public record PurchaseOrderItemAclResource(
        @Schema(description = "Line item unique identifier", example = "1")
        Long id,

        @Schema(description = "Optional inventory item identifier", example = "101")
        Long inventoryItemId,

        @Schema(description = "Purchased product name", example = "Rice")
        String productName,

        @Schema(description = "Purchased quantity", example = "25.00")
        BigDecimal quantity,

        @Schema(description = "Unit price", example = "4.50")
        BigDecimal unitPrice,

        @Schema(description = "Unit type", example = "kg")
        String unitType
) {
}
