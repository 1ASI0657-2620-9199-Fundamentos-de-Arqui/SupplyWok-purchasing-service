package aurora.supply_wok.platform.suppliers.application.internal.commandservices;

import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierCommandService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateSupplierCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpsertSupplierFromProfileCommand;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Default supplier command service implementation.
 */
@Service
public class SupplierCommandServiceImpl implements SupplierCommandService {

    private final SupplierRepository supplierRepository;

    public SupplierCommandServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Supplier handle(CreateSupplierCommand command) {
        var supplier = new Supplier(
                UUID.randomUUID(),
                command.name(),
                command.contactName(),
                command.email(),
                command.phone(),
                command.category(),
                command.linkedDate(),
                command.sla(),
                command.responseTime()
        );
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier handle(UpsertSupplierFromProfileCommand command) {
        validateProfileSupplier(command);

        var supplier = supplierRepository.findByEmailIgnoreCase(command.email().trim())
                .orElseGet(() -> new Supplier(
                        UUID.randomUUID(),
                        command.name().trim(),
                        command.contactName().trim(),
                        command.email().trim(),
                        command.phone().trim(),
                        normalizeOrDefault(command.category(), "General"),
                        LocalDate.now().toString(),
                        normalizeOrDefault(command.sla(), "24h"),
                        normalizeOrDefault(command.responseTime(), "2h")
                ));

        supplier.updateFromProfile(
                command.name().trim(),
                command.contactName().trim(),
                command.email().trim(),
                command.phone().trim(),
                normalizeOrDefault(command.category(), "General"),
                normalizeOrDefault(command.sla(), "24h"),
                normalizeOrDefault(command.responseTime(), "2h")
        );

        return supplierRepository.save(supplier);
    }

    private void validateProfileSupplier(UpsertSupplierFromProfileCommand command) {
        if (command.name() == null || command.name().isBlank()) {
            throw new IllegalArgumentException("Supplier name is required.");
        }
        if (command.contactName() == null || command.contactName().isBlank()) {
            throw new IllegalArgumentException("Supplier contact name is required.");
        }
        if (command.email() == null || command.email().isBlank()) {
            throw new IllegalArgumentException("Supplier email is required.");
        }
        if (command.phone() == null || command.phone().isBlank()) {
            throw new IllegalArgumentException("Supplier phone is required.");
        }
    }

    private String normalizeOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
