package aurora.supply_wok.platform.purchasing.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command payload for a purchase order line item.
 */
public record CreatePurchaseOrderItemCommand(
        Long id,
        Long inventoryItemId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        String unitType
) {
}
