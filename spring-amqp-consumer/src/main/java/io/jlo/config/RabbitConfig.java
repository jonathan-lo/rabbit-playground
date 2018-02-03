package io.jlo.config;

import brave.Tracing;
import brave.sampler.Sampler;
import brave.spring.amqp.TracingRabbitListenerAdvice;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
@EnableRabbit
public class RabbitConfig {

  public static final String RABBIT_PLAYGROUND_EXCHANGE = "rabbit-playground-exchange";

  @Bean
  public Exchange exchange() {
    return ExchangeBuilder.fanoutExchange(RABBIT_PLAYGROUND_EXCHANGE).build();
  }

  @Bean
  public Queue greetingQueue() {
    return new Queue("greetingQueue", true);
  }

  @Bean
  public Binding binding() {
    return BindingBuilder.bind(greetingQueue())
        .to(exchange())
        .with("greeting.*")
        .noargs();
  }

  @Bean
  public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory,
      TracingRabbitListenerAdvice tracingRabbitListenerAdvice) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setConcurrentConsumers(3);
    factory.setMaxConcurrentConsumers(10);
    factory.setAdviceChain(tracingRabbitListenerAdvice);
    return factory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setExchange(RABBIT_PLAYGROUND_EXCHANGE);
    return rabbitTemplate;
  }

  @Bean
  public TracingRabbitListenerAdvice tracingRabbitListenerAdvice(Tracing tracing) {
    return new TracingRabbitListenerAdvice(tracing);
  }

  @Bean
  public Tracing tracing(Reporter<zipkin2.Span> reporter) {
    return Tracing.newBuilder()
        .localServiceName("spring-amqp-consumer")
        .sampler(Sampler.ALWAYS_SAMPLE)
        .spanReporter(reporter)
        .build();
  }

  @Bean
  public AsyncReporter<zipkin2.Span> reporter() {
    return AsyncReporter.create(OkHttpSender.create("http://zipkin:9411/api/v2/spans"));
  }
}
