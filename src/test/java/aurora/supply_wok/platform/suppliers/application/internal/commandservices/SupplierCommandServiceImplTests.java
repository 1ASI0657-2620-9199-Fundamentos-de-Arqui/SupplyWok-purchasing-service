package aurora.supply_wok.platform.suppliers.application.internal.commandservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateSupplierCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpsertSupplierFromProfileCommand;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierCommandServiceImplTests {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierCommandServiceImpl supplierCommandService;

    @Test
    void handleCreate_whenValidCommand_savesAndReturnsSupplier() {
        // Arrange
        var command = new CreateSupplierCommand(
                "Verduras Express",
                "Carlos Sanchez",
                "carlos@verduras.com",
                "+51987654321",
                "Verduras",
                "2026-05-10",
                "24h",
                "2h"
        );
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = supplierCommandService.handle(command);

        // Assert
        assertThat(result.getName()).isEqualTo("Verduras Express");
        assertThat(result.getEmail()).isEqualTo("carlos@verduras.com");
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void handleUpsert_whenNameIsBlank_throwsIllegalArgumentException() {
        // Arrange
        var command = new UpsertSupplierFromProfileCommand(
                " ",
                "Carlos Sanchez",
                "carlos@verduras.com",
                "+51987654321",
                "Verduras",
                "24h",
                "2h"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> supplierCommandService.handle(command));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void handleUpsert_whenContactNameIsBlank_throwsIllegalArgumentException() {
        // Arrange
        var command = new UpsertSupplierFromProfileCommand(
                "Verduras Express",
                "",
                "carlos@verduras.com",
                "+51987654321",
                "Verduras",
                "24h",
                "2h"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> supplierCommandService.handle(command));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void handleUpsert_whenEmailIsBlank_throwsIllegalArgumentException() {
        // Arrange
        var command = new UpsertSupplierFromProfileCommand(
                "Verduras Express",
                "Carlos Sanchez",
                "  ",
                "+51987654321",
                "Verduras",
                "24h",
                "2h"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> supplierCommandService.handle(command));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void handleUpsert_whenPhoneIsBlank_throwsIllegalArgumentException() {
        // Arrange
        var command = new UpsertSupplierFromProfileCommand(
                "Verduras Express",
                "Carlos Sanchez",
                "carlos@verduras.com",
                "",
                "Verduras",
                "24h",
                "2h"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> supplierCommandService.handle(command));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void handleUpsert_whenSupplierDoesNotExist_createsAndSavesNewSupplier() {
        // Arrange
        var command = new UpsertSupplierFromProfileCommand(
                "Verduras Express",
                "Carlos Sanchez",
                "carlos@verduras.com",
                "+51987654321",
                "Verduras",
                "24h",
                "2h"
        );
        when(supplierRepository.findByEmailIgnoreCase("carlos@verduras.com")).thenReturn(Optional.empty());
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = supplierCommandService.handle(command);

        // Assert
        assertThat(result.getName()).isEqualTo("Verduras Express");
        assertThat(result.getEmail()).isEqualTo("carlos@verduras.com");
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void handleUpsert_whenSupplierAlreadyExists_updatesAndSavesExistingSupplier() {
        // Arrange
        var existingSupplier = new Supplier();
        existingSupplier.setId(10L);
        existingSupplier.setName("Old Supplier Name");

        var command = new UpsertSupplierFromProfileCommand(
                "Verduras Express",
                "Carlos Sanchez",
                "carlos@verduras.com",
                "+51987654321",
                "Verduras",
                "24h",
                "2h"
        );
        when(supplierRepository.findByEmailIgnoreCase("carlos@verduras.com")).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = supplierCommandService.handle(command);

        // Assert
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Verduras Express");
        verify(supplierRepository).save(existingSupplier);
    }
}
