package io.jlo.config;

import brave.Tracing;
import brave.sampler.Sampler;
import brave.spring.amqp.TracingMessagePostProcessor;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class RabbitConfig {

    private static final String RABBIT_PLAYGROUND_EXCHANGE = "rabbit-playground-exchange";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, TracingMessagePostProcessor tracingMessagePostProcessor) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(RABBIT_PLAYGROUND_EXCHANGE);
        rabbitTemplate.setBeforePublishPostProcessors(tracingMessagePostProcessor);
        return rabbitTemplate;
    }

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.fanoutExchange(RABBIT_PLAYGROUND_EXCHANGE).build();
    }

    @Bean
    public TracingMessagePostProcessor tracingMessagePostProcessor(Tracing tracing) {
        return new TracingMessagePostProcessor(tracing);
    }

    @Bean
    public Tracing tracing(Reporter<Span> reporter) {
        return Tracing.newBuilder()
            .localServiceName("spring-amqp-producer")
            .sampler(Sampler.ALWAYS_SAMPLE)
            .spanReporter(reporter)
            .build();
    }

    @Bean
    public Reporter<Span> reporter() {
        return AsyncReporter.create(OkHttpSender.create("http://zipkin:9411/api/v2/spans"));
    }
}
