package aurora.supply_wok.platform.suppliers.application.internal.eventhandlers;

import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import aurora.supply_wok.platform.suppliers.interfaces.acl.SuppliersContextFacade;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Event handler that reacts to supplier profile synchronization requests.
 */
@Service
public class SupplierProfileSyncEventHandler {

    private final SuppliersContextFacade suppliersContextFacade;

    public SupplierProfileSyncEventHandler(SuppliersContextFacade suppliersContextFacade) {
        this.suppliersContextFacade = suppliersContextFacade;
    }

    @EventListener
    public void on(SupplierProfileSyncRequestedIntegrationEvent event) {
        suppliersContextFacade.syncSupplierProfile(
                event.name(),
                event.contactName(),
                event.email(),
                event.phone()
        );
    }
}
