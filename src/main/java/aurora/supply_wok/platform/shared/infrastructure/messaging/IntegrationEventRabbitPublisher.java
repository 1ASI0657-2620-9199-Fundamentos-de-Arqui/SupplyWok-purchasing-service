package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Publishes integration events to the RabbitMQ topic exchange.
 */
@Component
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class IntegrationEventRabbitPublisher {

    private static final String EXCHANGE_NAME = "supplywok.events";
    private static final String ROUTING_KEY_ORDER_CREATED = "purchasing.order.created";
    private static final String ROUTING_KEY_ORDER_STATUS_CHANGED = "purchasing.order.status-changed";

    private final RabbitTemplate rabbitTemplate;

    public IntegrationEventRabbitPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void on(PurchaseOrderCreatedIntegrationEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_ORDER_CREATED, event);
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void on(PurchaseOrderStatusChangedIntegrationEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_ORDER_STATUS_CHANGED, event);
    }
}
