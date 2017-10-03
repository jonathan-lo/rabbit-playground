package io.jlo.config;

import io.jlo.service.GreetingListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
    @Bean
    public GreetingListener greetingListener() {
        return new GreetingListener();
    }
}
