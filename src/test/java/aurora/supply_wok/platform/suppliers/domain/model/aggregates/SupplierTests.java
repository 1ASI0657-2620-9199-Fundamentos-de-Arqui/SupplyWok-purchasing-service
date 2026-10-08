package aurora.supply_wok.platform.suppliers.domain.model.aggregates;

import aurora.supply_wok.platform.suppliers.domain.model.events.SupplierCreatedEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierTests {

    @Test
    void updateFromProfile_whenValidParameters_updatesSupplierProperties() {
        // Arrange
        var supplier = new Supplier(
                UUID.randomUUID(),
                "Initial Name",
                "Old Contact",
                "old@supplier.com",
                "123456",
                "General",
                "2026-01-01",
                "24h",
                "2h"
        );

        // Act
        supplier.updateFromProfile(
                "Updated Name",
                "New Contact",
                "new@supplier.com",
                "987654",
                "Vegetables",
                "12h",
                "1h"
        );

        // Assert
        assertThat(supplier.getName()).isEqualTo("Updated Name");
        assertThat(supplier.getContactName()).isEqualTo("New Contact");
        assertThat(supplier.getEmail()).isEqualTo("new@supplier.com");
        assertThat(supplier.getPhone()).isEqualTo("987654");
        assertThat(supplier.getCategory()).isEqualTo("Vegetables");
        assertThat(supplier.getSla()).isEqualTo("12h");
        assertThat(supplier.getResponseTime()).isEqualTo("1h");
    }

    @Test
    void onCreated_whenInvoked_registersSupplierCreatedEvent() {
        // Arrange
        var supplier = new Supplier();

        // Act
        supplier.onCreated();

        // Assert
        assertThat(supplier.domainEvents()).hasSize(1);
        assertThat(supplier.domainEvents().iterator().next()).isInstanceOf(SupplierCreatedEvent.class);
    }
}
