package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateSupplierCommand;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CreateSupplierResource;

import java.time.LocalDate;

/**
 * Maps create supplier requests to commands.
 */
public final class CreateSupplierCommandFromResourceAssembler {

    private CreateSupplierCommandFromResourceAssembler() {
    }

    public static CreateSupplierCommand toCommandFromResource(CreateSupplierResource resource) {
        var linkedDate = resource.linkedDate();
        if (linkedDate == null || linkedDate.isBlank()) {
            linkedDate = LocalDate.now().toString();
        }

        return new CreateSupplierCommand(
                resource.name().trim(),
                resource.contactName().trim(),
                resource.email().trim(),
                resource.phone().trim(),
                resource.category().trim(),
                linkedDate,
                resource.sla().trim(),
                resource.responseTime().trim()
        );
    }
}
