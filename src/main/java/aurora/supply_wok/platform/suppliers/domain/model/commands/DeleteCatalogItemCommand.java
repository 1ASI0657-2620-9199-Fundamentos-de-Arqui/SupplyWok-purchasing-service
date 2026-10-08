package aurora.supply_wok.platform.suppliers.domain.model.commands;

/**
 * Command to delete a catalog item.
 */
public record DeleteCatalogItemCommand(Long supplierId, Long catalogItemId) {
}
