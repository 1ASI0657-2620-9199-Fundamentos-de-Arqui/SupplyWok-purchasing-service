package aurora.supply_wok.platform.shared.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ messaging configuration for purchasing-service.
 */
@Configuration
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class MessagingConfiguration {

    public static final String EXCHANGE_NAME = "supplywok.events";
    public static final String QUEUE_NAME = "purchasing.events";
    public static final String ROUTING_KEY_PROFILES_PATTERN = "profiles.#";
    private static final String[] TRUSTED_PACKAGES = new String[] {"aurora.supply_wok.platform.purchasing.interfaces.events", "aurora.supply_wok.platform.profiles.interfaces.events"};

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue purchasingEventsQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding purchasingEventsBinding(Queue purchasingEventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(purchasingEventsQueue).to(eventsExchange).with(ROUTING_KEY_PROFILES_PATTERN);
    }

    @Bean
    @SuppressWarnings("removal")
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(TRUSTED_PACKAGES);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
