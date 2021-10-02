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

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
    private final RabbitTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RabbitListener(queues = "q.alternate")
    public void listen(Message message) {
        log.info("Received [{}]", message);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "q.binding-test"),
                    exchange = @Exchange(
                            name = "x.binding-test",
                            type = ExchangeTypes.DIRECT,
                            durable = "true",
                            autoDelete = "false",
                            arguments = {},
                            internal = "true",
                            ignoreDeclarationExceptions = "true"),
                    key = "binding-key")
    )
    public void example(Message message) {
        log.info("Received [{}]", message);
    }

    @Override
    public void run(String... args) {
        rabbitTemplate.convertAndSend("x.direct", "key-not-bound", "MSG");
    }
}
