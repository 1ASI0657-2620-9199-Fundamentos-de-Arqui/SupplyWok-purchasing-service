package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.SupplierResource;

/**
 * Maps supplier domain entities to REST resources.
 */
public final class SupplierResourceFromEntityAssembler {

    private SupplierResourceFromEntityAssembler() {
    }

    public static SupplierResource toResourceFromEntity(Supplier supplier) {
        return new SupplierResource(
                supplier.getId(),
                supplier.getUuid(),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getCategory(),
                supplier.getLinkedDate(),
                supplier.getSla(),
                supplier.getResponseTime()
        );
    }
}
