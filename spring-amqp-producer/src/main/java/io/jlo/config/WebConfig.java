package io.jlo.config;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.spring.webmvc.TracingHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Import(TracingHandlerInterceptor.class)
public class WebConfig extends WebMvcConfigurerAdapter {

  @Autowired
  private TracingHandlerInterceptor tracingHandlerInterceptor;

  @Bean
  public HttpTracing httpTracing(Tracing tracing) {
    return HttpTracing.create(tracing);
  }

  @Override public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tracingHandlerInterceptor);
  }
}
