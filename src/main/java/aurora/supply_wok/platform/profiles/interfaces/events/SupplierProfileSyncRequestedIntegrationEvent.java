package aurora.supply_wok.platform.profiles.interfaces.events;

/**
 * Integration event published when a supplier profile synchronization is requested.
 */
public record SupplierProfileSyncRequestedIntegrationEvent(
        String name,
        String contactName,
        String email,
        String phone
) {
}
