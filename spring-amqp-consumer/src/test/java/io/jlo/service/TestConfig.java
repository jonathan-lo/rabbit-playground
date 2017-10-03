package io.jlo.service;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RabbitListenerTest
public class TestConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory();
    }

    @Bean
    public GreetingListener greetingListener() {
        return new GreetingListener();
    }
}