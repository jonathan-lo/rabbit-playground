package io.jlo.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/greeting")
public class GreetingController {

    private final RabbitTemplate rabbitTemplate;

    public GreetingController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PutMapping
    public String sayHello(@RequestBody String name) {

        String output = String.format("Hello, %s!", name);
        rabbitTemplate.convertAndSend("greeting." + name, output);

        return output;
    }
}
