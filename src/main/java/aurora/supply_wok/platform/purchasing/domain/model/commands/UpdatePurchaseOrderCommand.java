package aurora.supply_wok.platform.purchasing.domain.model.commands;

import java.time.LocalDate;
import java.util.List;

/**
 * Command to update a purchase order.
 */
public record UpdatePurchaseOrderCommand(
        Long id,
        String code,
        Long supplierId,
        String supplierName,
        String restaurantName,
        LocalDate orderDate,
        LocalDate estimatedDate,
        String priority,
        String status,
        List<CreatePurchaseOrderItemCommand> items,
        Long restaurantId
) {
    public UpdatePurchaseOrderCommand(
            Long id,
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
        this(id, code, supplierId, supplierName, restaurantName, orderDate, estimatedDate, priority, status, items, null);
    }
}
