package aurora.supply_wok.platform.suppliers.interfaces.acl;

import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierClientCommandService;
import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierCommandService;
import aurora.supply_wok.platform.suppliers.application.queryservices.SupplierQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.commands.LinkClientToSupplierCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpsertSupplierFromProfileCommand;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllSuppliersQuery;
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
class SuppliersContextFacadeTests {

    @Mock
    private SupplierQueryService supplierQueryService;

    @Mock
    private SupplierCommandService supplierCommandService;

    @Mock
    private SupplierClientCommandService supplierClientCommandService;

    @InjectMocks
    private SuppliersContextFacade suppliersContextFacade;

    @Test
    void getSupplierIdentityById_whenSupplierIdIsZeroOrNegative_returnsEmpty() {
        // Arrange
        Long invalidSupplierId = 0L;

        // Act
        var result = suppliersContextFacade.getSupplierIdentityById(invalidSupplierId);

        // Assert
        assertThat(result).isEmpty();
        verify(supplierQueryService, never()).handle(any(GetAllSuppliersQuery.class));
    }

    @Test
    void getSupplierIdentityById_whenSupplierFound_returnsSupplierIdentity() {
        // Arrange
        Long supplierId = 8L;
        var supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Distribuidora Asia");

        when(supplierQueryService.handle(any(GetAllSuppliersQuery.class))).thenReturn(List.of(supplier));

        // Act
        var result = suppliersContextFacade.getSupplierIdentityById(supplierId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(supplierId);
        assertThat(result.get().name()).isEqualTo("Distribuidora Asia");
    }

    @Test
    void ensureSupplierClientLinked_whenCalled_delegatesToSupplierClientCommandService() {
        // Arrange
        Long supplierId = 8L;
        String clientName = "Chifa Kam Men";

        // Act
        suppliersContextFacade.ensureSupplierClientLinked(supplierId, clientName);

        // Assert
        verify(supplierClientCommandService).handle(any(LinkClientToSupplierCommand.class));
    }

    @Test
    void syncSupplierProfile_whenCalled_delegatesToSupplierCommandService() {
        // Arrange
        String name = "Asia Food";
        String contact = "Kenji Sato";
        String email = "kenji@asiafood.com";
        String phone = "+51999111333";

        // Act
        suppliersContextFacade.syncSupplierProfile(name, contact, email, phone);

        // Assert
        verify(supplierCommandService).handle(any(UpsertSupplierFromProfileCommand.class));
    }
}
