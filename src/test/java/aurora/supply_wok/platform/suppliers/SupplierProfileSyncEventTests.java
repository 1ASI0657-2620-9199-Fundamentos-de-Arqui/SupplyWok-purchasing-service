package aurora.supply_wok.platform.suppliers;

import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SupplierProfileSyncEventTests {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    void on_whenSupplierEmailDoesNotExist_createsNewSupplier() {
        // Arrange
        String testEmail = "test-sync-supplier@domain.com";
        var createEvent = new SupplierProfileSyncRequestedIntegrationEvent(
                "Fresh Vegetables Inc",
                "Maria Lopez",
                testEmail,
                "+51999888777"
        );

        // Act
        applicationEventPublisher.publishEvent(createEvent);

        // Assert
        var createdSupplier = supplierRepository.findByEmailIgnoreCase(testEmail);
        assertThat(createdSupplier).isPresent();
        assertThat(createdSupplier.get().getName()).isEqualTo("Fresh Vegetables Inc");
        assertThat(createdSupplier.get().getContactName()).isEqualTo("Maria Lopez");
        assertThat(createdSupplier.get().getPhone()).isEqualTo("+51999888777");
    }

    @Test
    void on_whenSupplierEmailAlreadyExists_updatesExistingSupplier() {
        // Arrange
        String testEmail = "test-sync-supplier-update@domain.com";
        var initialEvent = new SupplierProfileSyncRequestedIntegrationEvent(
                "Fresh Vegetables Inc",
                "Maria Lopez",
                testEmail,
                "+51999888777"
        );
        applicationEventPublisher.publishEvent(initialEvent);
        var initialSupplier = supplierRepository.findByEmailIgnoreCase(testEmail).orElseThrow();

        var updateEvent = new SupplierProfileSyncRequestedIntegrationEvent(
                "Fresh Vegetables Corporation",
                "Maria Lopez Rodriguez",
                testEmail,
                "+51999111222"
        );

        // Act
        applicationEventPublisher.publishEvent(updateEvent);

        // Assert
        var updatedSupplier = supplierRepository.findByEmailIgnoreCase(testEmail);
        assertThat(updatedSupplier).isPresent();
        assertThat(updatedSupplier.get().getId()).isEqualTo(initialSupplier.getId());
        assertThat(updatedSupplier.get().getName()).isEqualTo("Fresh Vegetables Corporation");
        assertThat(updatedSupplier.get().getContactName()).isEqualTo("Maria Lopez Rodriguez");
        assertThat(updatedSupplier.get().getPhone()).isEqualTo("+51999111222");
    }
}
