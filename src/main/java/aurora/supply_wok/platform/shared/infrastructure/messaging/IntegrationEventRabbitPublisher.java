package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

/**
 * Publishes integration events to the RabbitMQ topic exchange.
 */
@Component
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class IntegrationEventRabbitPublisher {

    private static final String ROUTING_KEY_ORDER_CREATED = "purchasing.order.created";
    private static final String ROUTING_KEY_ORDER_STATUS_CHANGED = "purchasing.order.status-changed";

    private final EventOutbox outbox;

    public IntegrationEventRabbitPublisher(EventOutbox outbox) {
        this.outbox = outbox;
    }

    @EventListener
    public void on(PurchaseOrderCreatedIntegrationEvent event) {
        outbox.enqueue(ROUTING_KEY_ORDER_CREATED, event);
    }

    @EventListener
    public void on(PurchaseOrderStatusChangedIntegrationEvent event) {
        outbox.enqueue(ROUTING_KEY_ORDER_STATUS_CHANGED, event);
    }
}
