package aurora.supply_wok.platform.suppliers.application.internal.queryservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllSuppliersQuery;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierQueryServiceImplTests {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierQueryServiceImpl supplierQueryService;

    @Test
    void handle_whenGetAllSuppliersQuery_delegatesToRepository() {
        // Arrange
        var query = new GetAllSuppliersQuery();
        var suppliers = List.of(new Supplier(), new Supplier());
        when(supplierRepository.findAll()).thenReturn(suppliers);

        // Act
        var result = supplierQueryService.handle(query);

        // Assert
        assertThat(result).hasSize(2);
        verify(supplierRepository).findAll();
    }
}
