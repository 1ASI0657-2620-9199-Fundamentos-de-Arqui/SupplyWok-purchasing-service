package aurora.supply_wok.platform.suppliers.application.internal.commandservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.DeleteCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpdateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogItemCommandServiceImplTests {

    @Mock
    private CatalogItemRepository catalogItemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private CatalogItemCommandServiceImpl catalogItemCommandService;

    @Test
    void handleCreate_whenSupplierDoesNotExist_returnsEmptyAndNeverSaves() {
        // Arrange
        var command = new CreateCatalogItemCommand(
                1L,
                "Salsa Hoi Sin",
                "Salsas",
                BigDecimal.valueOf(18.5),
                ECatalogUnit.LTR,
                "Entrega inmediata"
        );
        when(supplierRepository.existsById(1L)).thenReturn(false);

        // Act
        var result = catalogItemCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(catalogItemRepository, never()).save(any());
    }

    @Test
    void handleCreate_whenSupplierExists_savesAndReturnsItemId() {
        // Arrange
        var command = new CreateCatalogItemCommand(
                1L,
                "Salsa Hoi Sin",
                "Salsas",
                BigDecimal.valueOf(18.5),
                ECatalogUnit.LTR,
                "Entrega inmediata"
        );
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(catalogItemRepository.save(any(CatalogItem.class))).thenAnswer(invocation -> {
            CatalogItem item = invocation.getArgument(0);
            item.setId(42L);
            return item;
        });

        // Act
        var result = catalogItemCommandService.handle(command);

        // Assert
        assertThat(result).contains(42L);
        verify(catalogItemRepository).save(any(CatalogItem.class));
    }

    @Test
    void handleUpdate_whenSupplierDoesNotExist_returnsEmpty() {
        // Arrange
        var command = new UpdateCatalogItemCommand(
                1L,
                42L,
                "Salsa Hoi Sin",
                "Salsas",
                BigDecimal.valueOf(20.0),
                ECatalogUnit.LTR,
                "Entrega inmediata"
        );
        when(supplierRepository.existsById(1L)).thenReturn(false);

        // Act
        var result = catalogItemCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(catalogItemRepository, never()).save(any());
    }

    @Test
    void handleUpdate_whenCatalogItemNotFound_returnsEmpty() {
        // Arrange
        var command = new UpdateCatalogItemCommand(
                1L,
                42L,
                "Salsa Hoi Sin",
                "Salsas",
                BigDecimal.valueOf(20.0),
                ECatalogUnit.LTR,
                "Entrega inmediata"
        );
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(catalogItemRepository.findByIdAndSupplierId(42L, 1L)).thenReturn(Optional.empty());

        // Act
        var result = catalogItemCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(catalogItemRepository, never()).save(any());
    }

    @Test
    void handleUpdate_whenItemExists_savesAndReturnsUpdatedItem() {
        // Arrange
        var existingItem = new CatalogItem(
                1L,
                "Salsa Hoi Sin",
                "Salsas",
                BigDecimal.valueOf(18.5),
                ECatalogUnit.LTR,
                "Entrega inmediata"
        );
        existingItem.setId(42L);

        var command = new UpdateCatalogItemCommand(
                1L,
                42L,
                "Salsa Hoi Sin Premium",
                "Salsas Premium",
                BigDecimal.valueOf(25.0),
                ECatalogUnit.LTR,
                "Entrega 24h"
        );
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(catalogItemRepository.findByIdAndSupplierId(42L, 1L)).thenReturn(Optional.of(existingItem));
        when(catalogItemRepository.save(any(CatalogItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = catalogItemCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Salsa Hoi Sin Premium");
        assertThat(result.get().getPrice()).isEqualTo(new BigDecimal("25.00"));
        verify(catalogItemRepository).save(existingItem);
    }

    @Test
    void handleDelete_whenSupplierDoesNotExist_returnsFalse() {
        // Arrange
        var command = new DeleteCatalogItemCommand(1L, 42L);
        when(supplierRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean deleted = catalogItemCommandService.handle(command);

        // Assert
        assertThat(deleted).isFalse();
        verify(catalogItemRepository, never()).delete(any());
    }

    @Test
    void handleDelete_whenCatalogItemNotFound_returnsFalse() {
        // Arrange
        var command = new DeleteCatalogItemCommand(1L, 42L);
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(catalogItemRepository.findByIdAndSupplierId(42L, 1L)).thenReturn(Optional.empty());

        // Act
        boolean deleted = catalogItemCommandService.handle(command);

        // Assert
        assertThat(deleted).isFalse();
        verify(catalogItemRepository, never()).delete(any());
    }

    @Test
    void handleDelete_whenCatalogItemExists_deletesAndReturnsTrue() {
        // Arrange
        var existingItem = new CatalogItem();
        existingItem.setId(42L);

        var command = new DeleteCatalogItemCommand(1L, 42L);
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(catalogItemRepository.findByIdAndSupplierId(42L, 1L)).thenReturn(Optional.of(existingItem));

        // Act
        boolean deleted = catalogItemCommandService.handle(command);

        // Assert
        assertThat(deleted).isTrue();
        verify(catalogItemRepository).delete(existingItem);
    }
}
