package aurora.supply_wok.platform.purchasing.application.internal.eventhandlers;

import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderCreatedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderStatusChangedEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Publishes purchase-order domain events as integration events.
 */
@Service
public class PurchaseOrderDomainEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public PurchaseOrderDomainEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void on(PurchaseOrderCreatedEvent event) {
        eventPublisher.publishEvent(new PurchaseOrderCreatedIntegrationEvent(
                event.purchaseOrderId(),
                event.code(),
                event.supplierId(),
                event.supplierName(),
                event.restaurantName(),
                event.priority(),
                event.status()
        ));
    }

    @EventListener
    public void on(PurchaseOrderStatusChangedEvent event) {
        eventPublisher.publishEvent(new PurchaseOrderStatusChangedIntegrationEvent(
                event.purchaseOrderId(),
                event.code(),
                event.supplierId(),
                event.supplierName(),
                event.restaurantName(),
                event.previousStatus().name(),
                event.currentStatus().name()
        ));
    }
}
