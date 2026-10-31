package aurora.supply_wok.platform.shared.infrastructure.messaging;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class EventOutbox {
    private final JdbcTemplate database;
    private final MessageConverter converter;

    public EventOutbox(JdbcTemplate database, MessageConverter converter) {
        this.database = database;
        this.converter = converter;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String routingKey, Object event) {
        var message = converter.toMessage(event, new MessageProperties());
        database.update("""
                INSERT INTO outbox_events (event_id, routing_key, event_type, payload, occurred_at)
                VALUES (?, ?, ?, ?, ?)
                """, UUID.randomUUID().toString(), routingKey, event.getClass().getName(),
                new String(message.getBody(), StandardCharsets.UTF_8), System.currentTimeMillis());
    }
}
