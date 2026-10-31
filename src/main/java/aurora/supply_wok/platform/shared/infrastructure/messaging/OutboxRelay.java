package aurora.supply_wok.platform.shared.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelay {
    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private final JdbcTemplate database;
    private final RabbitTemplate broker;
    private final TransactionTemplate transactions;

    public OutboxRelay(JdbcTemplate database, RabbitTemplate broker, TransactionTemplate transactions) {
        this.database = database;
        this.broker = broker;
        this.transactions = transactions;
        broker.setMandatory(true);
    }

    private record Pending(String id, String route, String type, String payload, long created, int attempts) {}

    @Scheduled(fixedDelayString = "${supplywok.outbox.poll-ms:500}")
    public void flush() {
        for (int i = 0; i < 32; i++) {
            if (!Boolean.TRUE.equals(transactions.execute(status -> publishNext()))) return;
        }
    }

    private boolean publishNext() {
        var pending = database.query("""
                SELECT event_id, routing_key, event_type, payload, occurred_at, attempts
                FROM outbox_events WHERE next_attempt <= ? ORDER BY occurred_at, event_id
                LIMIT 1 FOR UPDATE SKIP LOCKED
                """, (rs, i) -> new Pending(rs.getString(1), rs.getString(2), rs.getString(3),
                rs.getString(4), rs.getLong(5), rs.getInt(6)), System.currentTimeMillis());
        if (pending.isEmpty()) return false;
        var event = pending.getFirst();
        var properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        properties.setMessageId(event.id());
        properties.setHeader("__TypeId__", event.type());
        properties.setHeader("eventType", event.route());
        properties.setHeader("version", 1);
        properties.setHeader("occurredAt", Instant.ofEpochMilli(event.created()).toString());
        var confirmation = new CorrelationData(UUID.randomUUID().toString());
        try {
            broker.send("supplywok.events", event.route(),
                    new Message(event.payload().getBytes(StandardCharsets.UTF_8), properties), confirmation);
            if (!confirmation.getFuture().get(3, TimeUnit.SECONDS).ack() || confirmation.getReturned() != null)
                throw new IllegalStateException("Broker did not route the event");
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) Thread.currentThread().interrupt();
            long delay = Math.min(30000L, 1000L << Math.min(event.attempts(), 5));
            database.update("UPDATE outbox_events SET attempts=attempts+1, next_attempt=? WHERE event_id=?",
                    System.currentTimeMillis() + delay, event.id());
            log.warn("Outbox retry eventId={} attempt={} cause={}", event.id(), event.attempts()+1, ex.getClass().getSimpleName());
            return false;
        }
        database.update("DELETE FROM outbox_events WHERE event_id=?", event.id());
        log.info("Outbox delivered eventId={} routingKey={}", event.id(), event.route());
        return true;
    }
}
