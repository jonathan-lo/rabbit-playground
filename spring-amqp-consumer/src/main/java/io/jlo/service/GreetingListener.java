package io.jlo.service;

import java.util.logging.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GreetingListener {

    private static final Logger LOG = Logger.getLogger(GreetingListener.class.getName());

    @RabbitListener(id = "greetingListener", queues = "#{greetingQueue.name}")
    public void handleGreeting(String greeting) throws Exception {
        LOG.info("received greeting = " + greeting);
        simulateProcessing();
        LOG.info("finished processing greeting");
    }

    private void simulateProcessing() throws InterruptedException {
        long delay = (long) (2000L * Math.random());
        Thread.sleep(delay);
    }
}
