package pl.akolata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static pl.akolata.config.RabbitMqConfig.ALTERNATE_EXCHANGE_QUEUE;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
    private final RabbitTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RabbitListener(queues = ALTERNATE_EXCHANGE_QUEUE)
    public void listen(Message message) {
        log.info("Queue [{}] received the message [{}]", ALTERNATE_EXCHANGE_QUEUE, message);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "q.listener-declaration"),
                    exchange = @Exchange(
                            name = "x.listener-declaration",
                            type = ExchangeTypes.DIRECT,
                            durable = "false",
                            autoDelete = "true",
                            arguments = {},
                            internal = "false",
                            ignoreDeclarationExceptions = "true"),
                    key = "my-key")
    )
    public void example(Message message) {
        log.info("Received [{}]", message);
    }

    @Override
    public void run(String... args) {
        rabbitTemplate.convertAndSend("x.direct", "key-not-bound", "MSG");
    }
}
