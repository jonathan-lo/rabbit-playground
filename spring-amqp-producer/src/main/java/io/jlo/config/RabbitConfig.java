package io.jlo.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    private static final String RABBIT_PLAYGROUND_EXCHANGE = "rabbit-playground-exchange";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(RABBIT_PLAYGROUND_EXCHANGE);
        return rabbitTemplate;
    }

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.fanoutExchange(RABBIT_PLAYGROUND_EXCHANGE).build();
    }
}
