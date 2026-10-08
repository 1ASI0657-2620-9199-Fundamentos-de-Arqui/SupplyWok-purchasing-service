package aurora.supply_wok.platform.purchasing.interfaces.acl.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Purchase order payload exposed through the Purchasing ACL.
 */
@Schema(name = "PurchaseOrderAclResource", description = "Purchase order exposed through the Purchasing ACL")
public record PurchaseOrderAclResource(
        @Schema(description = "Purchase order unique identifier", example = "1")
        Long id,

        @Schema(description = "Purchase order code", example = "PO-24021")
        String code,

        @Schema(description = "Supplier unique identifier", example = "201")
        Long supplierId,

        @Schema(description = "Supplier display name", example = "Golden Wok Produce")
        String supplierName,

        @Schema(description = "Restaurant display name", example = "Gran Dragon Chifa")
        String restaurantName,

        @Schema(description = "Order date", example = "2026-05-10")
        String orderDate,

        @Schema(description = "Estimated delivery date", example = "2026-05-11")
        String estimatedDate,

        @Schema(description = "Purchase order priority", example = "High")
        String priority,

        @Schema(description = "Purchase order status", example = "Pending")
        String status,

        @Schema(description = "Purchase order line items")
        List<PurchaseOrderItemAclResource> items
) {
}
