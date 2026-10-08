package aurora.supply_wok.platform.suppliers.application.internal.queryservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllCatalogItemsBySupplierIdQuery;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetCatalogItemByIdQuery;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogItemQueryServiceImplTests {

    @Mock
    private CatalogItemRepository catalogItemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private CatalogItemQueryServiceImpl catalogItemQueryService;

    @Test
    void handleGetAll_whenSupplierDoesNotExist_returnsEmpty() {
        // Arrange
        var query = new GetAllCatalogItemsBySupplierIdQuery(1L);
        when(supplierRepository.existsById(1L)).thenReturn(false);

        // Act
        var result = catalogItemQueryService.handle(query);

        // Assert
        assertThat(result).isEmpty();
        verify(catalogItemRepository, never()).findAllBySupplierId(any());
    }

    @Test
    void handleGetAll_whenSupplierExists_delegatesToRepository() {
        // Arrange
        var query = new GetAllCatalogItemsBySupplierIdQuery(1L);
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(catalogItemRepository.findAllBySupplierId(1L)).thenReturn(List.of(new CatalogItem()));

        // Act
        var result = catalogItemQueryService.handle(query);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        verify(catalogItemRepository).findAllBySupplierId(1L);
    }

    @Test
    void handleGetById_whenSupplierDoesNotExist_returnsEmpty() {
        // Arrange
        var query = new GetCatalogItemByIdQuery(1L, 10L);
        when(supplierRepository.existsById(1L)).thenReturn(false);

        // Act
        var result = catalogItemQueryService.handle(query);

        // Assert
        assertThat(result).isEmpty();
        verify(catalogItemRepository, never()).findByIdAndSupplierId(any(), any());
    }

    @Test
    void handleGetById_whenSupplierExists_delegatesToRepository() {
        // Arrange
        var query = new GetCatalogItemByIdQuery(1L, 10L);
        var item = new CatalogItem();
        item.setId(10L);
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(catalogItemRepository.findByIdAndSupplierId(10L, 1L)).thenReturn(Optional.of(item));

        // Act
        var result = catalogItemQueryService.handle(query);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(10L);
        verify(catalogItemRepository).findByIdAndSupplierId(10L, 1L);
    }
}
