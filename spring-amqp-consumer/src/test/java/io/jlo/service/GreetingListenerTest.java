package io.jlo.service;

import io.jlo.config.RabbitConfig;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.junit.BrokerRunning;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.mockito.LatchCountDownAndCallRealMethodAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ContextConfiguration(
        classes = {
                RabbitConfig.class,
                TestConfig.class
        }
)
@RunWith(SpringRunner.class)
@SpringBootTest
public class GreetingListenerTest {

    @Autowired
    private RabbitListenerTestHarness testHarness;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private GreetingListener greetingListener;

    @ClassRule
    public static BrokerRunning brokerRunning = BrokerRunning.isRunningWithEmptyQueues("greeting");

    @Before
    public void getListener() {
        greetingListener = testHarness.getSpy("greetingListener");
    }

    @Test
    public void receivesMessage() throws Exception {
        LatchCountDownAndCallRealMethodAnswer answer = new LatchCountDownAndCallRealMethodAnswer(1);
        doAnswer(answer).when(greetingListener).handleGreeting(anyString());

        rabbitTemplate.convertAndSend("greeting.world", "Hello, world!");

        assertTrue(answer.getLatch().await(2, TimeUnit.SECONDS));
        verify(greetingListener).handleGreeting("Hello, world!");
    }

}