package aurora.supply_wok.platform.suppliers.application.internal.commandservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.SupplierClient;
import aurora.supply_wok.platform.suppliers.domain.model.commands.LinkClientToSupplierCommand;
import aurora.supply_wok.platform.suppliers.domain.repositories.ClientRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierClientRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierClientCommandServiceImplTests {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SupplierClientRepository supplierClientRepository;

    @InjectMocks
    private SupplierClientCommandServiceImpl supplierClientCommandService;

    @Test
    void handle_whenSupplierIdIsInvalid_returnsEmpty() {
        // Arrange
        var command = new LinkClientToSupplierCommand(0L, "Restaurant A");

        // Act
        var result = supplierClientCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(supplierRepository, never()).existsById(any());
    }

    @Test
    void handle_whenClientNameIsBlank_returnsEmpty() {
        // Arrange
        var command = new LinkClientToSupplierCommand(1L, "  ");

        // Act
        var result = supplierClientCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(supplierRepository, never()).existsById(any());
    }

    @Test
    void handle_whenSupplierDoesNotExist_returnsEmpty() {
        // Arrange
        var command = new LinkClientToSupplierCommand(1L, "Restaurant A");
        when(supplierRepository.existsById(1L)).thenReturn(false);

        // Act
        var result = supplierClientCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(clientRepository, never()).findByNameIgnoreCase(any());
    }

    @Test
    void handle_whenClientDoesNotExistAndLinkDoesNotExist_createsBoth() {
        // Arrange
        var command = new LinkClientToSupplierCommand(5L, "Wok House");
        when(supplierRepository.existsById(5L)).thenReturn(true);
        when(clientRepository.findByNameIgnoreCase("Wok House")).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            c.setId(10L);
            return c;
        });
        when(supplierClientRepository.existsBySupplierIdAndClientId(5L, 10L)).thenReturn(false);

        // Act
        var result = supplierClientCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Wok House");
        verify(clientRepository).save(any(Client.class));
        verify(supplierClientRepository).save(any(SupplierClient.class));
    }

    @Test
    void handle_whenClientAndLinkAlreadyExist_doesNotCreateDuplicateLink() {
        // Arrange
        var existingClient = new Client("Wok House", "Miraflores", "active");
        existingClient.setId(10L);

        var command = new LinkClientToSupplierCommand(5L, "Wok House");
        when(supplierRepository.existsById(5L)).thenReturn(true);
        when(clientRepository.findByNameIgnoreCase("Wok House")).thenReturn(Optional.of(existingClient));
        when(supplierClientRepository.existsBySupplierIdAndClientId(5L, 10L)).thenReturn(true);

        // Act
        var result = supplierClientCommandService.handle(command);

        // Assert
        assertThat(result).contains(existingClient);
        verify(clientRepository, never()).save(any());
        verify(supplierClientRepository, never()).save(any());
    }
}
