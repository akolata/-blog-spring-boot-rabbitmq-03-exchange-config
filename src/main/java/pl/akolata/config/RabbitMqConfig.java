package pl.akolata.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqConfig {
    private static final boolean DURABLE = false;
    private static final boolean AUTO_DELETE = false;
    private static final Map<String, Object> EMPTY_ARGUMENTS = Map.of();

    @Bean
    public Declarables declareExchanges() {
        return new Declarables(
                new DirectExchange("x.direct", DURABLE, AUTO_DELETE, Map.of("alternate-exchange", "x.alternate")),
                new FanoutExchange("x.fanout", DURABLE, AUTO_DELETE, EMPTY_ARGUMENTS),
                new TopicExchange("x.topic", DURABLE, AUTO_DELETE),
                new HeadersExchange("x.headers", DURABLE, AUTO_DELETE, EMPTY_ARGUMENTS)
        );
    }

    @Bean
    public Declarables alternateExchange() {
        FanoutExchange alternateExchange = new FanoutExchange("x.alternate", true, false);
        Queue alternateQueue = new Queue("q.alternate");
        return new Declarables(
                alternateExchange,
                alternateQueue,
                BindingBuilder.bind(alternateQueue).to(alternateExchange)
        );
    }

}
