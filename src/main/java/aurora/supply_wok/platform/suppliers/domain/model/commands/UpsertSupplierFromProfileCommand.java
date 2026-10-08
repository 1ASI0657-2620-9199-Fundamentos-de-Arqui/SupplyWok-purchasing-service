package aurora.supply_wok.platform.suppliers.domain.model.commands;

/**
 * Command to create or update a supplier directory entry from a supplier profile.
 */
public record UpsertSupplierFromProfileCommand(
        String name,
        String contactName,
        String email,
        String phone,
        String category,
        String sla,
        String responseTime
) {
}
