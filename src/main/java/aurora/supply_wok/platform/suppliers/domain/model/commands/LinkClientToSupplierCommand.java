package aurora.supply_wok.platform.suppliers.domain.model.commands;

/**
 * Command to ensure a restaurant client is linked to a supplier.
 */
public record LinkClientToSupplierCommand(
        Long supplierId,
        String clientName
) {
}
