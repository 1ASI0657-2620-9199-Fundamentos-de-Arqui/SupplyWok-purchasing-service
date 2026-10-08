package aurora.supply_wok.platform.suppliers.application.internal.eventhandlers;

import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import aurora.supply_wok.platform.suppliers.interfaces.acl.SuppliersContextFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SupplierProfileSyncEventHandlerTests {

    @Mock
    private SuppliersContextFacade suppliersContextFacade;

    @InjectMocks
    private SupplierProfileSyncEventHandler handler;

    @Test
    void on_whenSupplierProfileSyncRequestedIntegrationEvent_delegatesToFacade() {
        // Arrange
        var event = new SupplierProfileSyncRequestedIntegrationEvent(
                "Fresh Herbs",
                "Rosa Gomez",
                "rosa@herbs.com",
                "+51988112233"
        );

        // Act
        handler.on(event);

        // Assert
        verify(suppliersContextFacade).syncSupplierProfile(
                "Fresh Herbs",
                "Rosa Gomez",
                "rosa@herbs.com",
                "+51988112233"
        );
    }
}
