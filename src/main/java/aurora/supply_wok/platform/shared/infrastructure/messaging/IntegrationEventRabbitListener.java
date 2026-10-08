package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Listens for integration events from RabbitMQ and republishes them locally.
 */
@Component
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class IntegrationEventRabbitListener {

    private final ApplicationEventPublisher applicationEventPublisher;

    public IntegrationEventRabbitListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RabbitListener(queues = "purchasing.events")
    public void on(SupplierProfileSyncRequestedIntegrationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
