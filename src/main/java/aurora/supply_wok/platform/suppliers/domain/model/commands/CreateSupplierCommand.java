package aurora.supply_wok.platform.suppliers.domain.model.commands;

/**
 * Command to create a new supplier.
 */
public record CreateSupplierCommand(
        String name,
        String contactName,
        String email,
        String phone,
        String category,
        String linkedDate,
        String sla,
        String responseTime
) {
}
