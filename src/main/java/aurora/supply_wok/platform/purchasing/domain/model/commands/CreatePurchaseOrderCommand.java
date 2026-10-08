package aurora.supply_wok.platform.purchasing.domain.model.commands;

import java.time.LocalDate;
import java.util.List;

/**
 * Command to create a purchase order.
 */
public record CreatePurchaseOrderCommand(
        String code,
        Long supplierId,
        String supplierName,
        String restaurantName,
        LocalDate orderDate,
        LocalDate estimatedDate,
        String priority,
        String status,
        List<CreatePurchaseOrderItemCommand> items
) {
}
