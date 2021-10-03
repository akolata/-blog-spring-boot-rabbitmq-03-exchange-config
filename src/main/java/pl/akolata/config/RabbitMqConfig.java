package pl.akolata.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqConfig {
    public static final String ALTERNATE_EXCHANGE_NAME = "x.alternate";
    public static final String ALTERNATE_EXCHANGE_QUEUE = "q.alternate";

    private static final boolean DURABLE = true;
    private static final boolean TRANSIENT = false;
    private static final boolean AUTO_DELETED = true;
    private static final boolean MANUALLY_DELETED = false;
    private static final Map<String, Object> EMPTY_ARGUMENTS = Map.of();

    @Bean
    public Declarables declareExchanges() {
        return new Declarables(
                new DirectExchange("x.direct", DURABLE, AUTO_DELETED, Map.of("alternate-exchange", ALTERNATE_EXCHANGE_NAME)),
                new FanoutExchange("x.fanout", DURABLE, AUTO_DELETED, EMPTY_ARGUMENTS),
                new TopicExchange("x.topic", TRANSIENT, MANUALLY_DELETED),
                new HeadersExchange("x.headers", TRANSIENT, MANUALLY_DELETED, EMPTY_ARGUMENTS)
        );
    }

    @Bean
    public Declarables alternateExchange() {
        FanoutExchange alternateExchange = new FanoutExchange(ALTERNATE_EXCHANGE_NAME, true, false);
        Queue alternateQueue = new Queue(ALTERNATE_EXCHANGE_QUEUE);
        return new Declarables(
                alternateExchange,
                alternateQueue,
                BindingBuilder.bind(alternateQueue).to(alternateExchange)
        );
    }

    @Bean
    public Declarables internalExchange() {
        FanoutExchange exchange = new FanoutExchange("x.internal");
        exchange.setInternal(true);
        exchange.setShouldDeclare(true);
        exchange.setIgnoreDeclarationExceptions(true);
        exchange.setDelayed(false);
        exchange.addArgument("key", "value");

        Queue queue = new Queue("q.internal");
        return new Declarables(
                exchange,
                queue,
                BindingBuilder.bind(queue).to(exchange)
        );
    }

}
