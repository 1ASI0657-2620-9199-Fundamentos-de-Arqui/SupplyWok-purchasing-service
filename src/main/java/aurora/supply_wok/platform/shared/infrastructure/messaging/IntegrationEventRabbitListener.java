package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listens for integration events from RabbitMQ and republishes them locally.
 */
@Component
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class IntegrationEventRabbitListener {

    private final JdbcTemplate database;
    private final ApplicationEventPublisher applicationEventPublisher;

    public IntegrationEventRabbitListener(ApplicationEventPublisher applicationEventPublisher, JdbcTemplate database) {
        this.database = database;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RabbitListener(queues = "purchasing.events")
    @Transactional
    public void on(SupplierProfileSyncRequestedIntegrationEvent event, @Header(value = AmqpHeaders.MESSAGE_ID, required = false) String eventId) {
        dispatch(event, eventId);
    }
    private void dispatch(Object event, String eventId) {
        // Messages produced before Sprint 2 have no stable delivery identifier.
        if (eventId != null && database.update(
                "INSERT INTO processed_events(event_id) VALUES (?) ON CONFLICT DO NOTHING", eventId) == 0) return;
        applicationEventPublisher.publishEvent(event);
    }
}
