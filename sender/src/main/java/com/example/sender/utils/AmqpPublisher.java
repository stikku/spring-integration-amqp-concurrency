package com.example.sender.utils;

import com.example.sender.configurations.properties.AMQPProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AmqpPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final AMQPProperties amqpProperties;

    public AmqpPublisher(RabbitTemplate rabbitTemplate, AMQPProperties amqpProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpProperties = amqpProperties;
    }

    public void send(final String routingkey, final String payload) {

        System.out.println("publishing: " + payload);

        rabbitTemplate.convertAndSend(amqpProperties.getExchange(), routingkey, payload, m -> {
            m.getMessageProperties().setHeader("routingKey", routingkey);
            return m;
        });
    }

}
