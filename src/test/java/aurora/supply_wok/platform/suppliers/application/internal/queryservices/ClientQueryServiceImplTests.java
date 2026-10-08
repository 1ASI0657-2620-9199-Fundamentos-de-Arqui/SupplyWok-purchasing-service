package aurora.supply_wok.platform.suppliers.application.internal.queryservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllClientsBySupplierIdQuery;
import aurora.supply_wok.platform.suppliers.domain.repositories.ClientRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceImplTests {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ClientQueryServiceImpl clientQueryService;

    @Test
    void handle_whenSupplierDoesNotExist_returnsEmpty() {
        // Arrange
        var query = new GetAllClientsBySupplierIdQuery(10L);
        when(supplierRepository.existsById(10L)).thenReturn(false);

        // Act
        var result = clientQueryService.handle(query);

        // Assert
        assertThat(result).isEmpty();
        verify(clientRepository, never()).findAllBySupplierId(any());
    }

    @Test
    void handle_whenSupplierExists_delegatesToRepository() {
        // Arrange
        var query = new GetAllClientsBySupplierIdQuery(10L);
        var clients = List.of(new Client("Restaurant X", "Lima", "active"));
        when(supplierRepository.existsById(10L)).thenReturn(true);
        when(clientRepository.findAllBySupplierId(10L)).thenReturn(clients);

        // Act
        var result = clientQueryService.handle(query);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        verify(clientRepository).findAllBySupplierId(10L);
    }
}
