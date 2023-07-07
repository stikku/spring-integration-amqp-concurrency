package com.example.sender.controllers;

import com.example.sender.utils.AmqpPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/publish")
public class Controller {

    private final AmqpPublisher amqpStandardPublisher;

    public Controller(final AmqpPublisher amqpStandardPublisher) {
        this.amqpStandardPublisher = amqpStandardPublisher;
    }

    @GetMapping("/sac")
    public String publishSingleActiveConsumer() {
        // Publish 20 indexed messages
        for (int i = 0; i<20; i++) {
            amqpStandardPublisher.send("routingkey.single_active_consumer","Index: " + i);
        }
        return "published";
    }

    @GetMapping("/exclusive")
    public String publish() {
        // Publish 20 indexed messages
        for (int i = 0; i<20; i++) {
            amqpStandardPublisher.send("routingkey.exclusive_consumer","Index: " + i);
        }
        return "published";
    }
}
