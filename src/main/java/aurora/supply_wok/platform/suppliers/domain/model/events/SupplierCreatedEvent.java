package aurora.supply_wok.platform.suppliers.domain.model.events;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;

public record SupplierCreatedEvent(
        Long supplierId,
        String name,
        String contactName,
        String email,
        String category
) {
    public static SupplierCreatedEvent from(Supplier supplier) {
        return new SupplierCreatedEvent(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getEmail(),
                supplier.getCategory()
        );
    }
}
