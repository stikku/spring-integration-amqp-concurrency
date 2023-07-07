package com.example.sender.configurations;

import com.example.sender.configurations.properties.AMQPProperties;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AmqpConfiguration {

    @Autowired
    private AMQPProperties props;

    @Bean
    Exchange exchange() {
        return new TopicExchange(props.getExchange(), true, false);
    }

    @Bean
    RabbitTemplate rabbitmqTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // Force publishing to always use a separate connections from consumerism.
        rabbitTemplate.setUsePublisherConnection(true);


        return rabbitTemplate;
    }

    @Bean
    ConnectionFactory connectionFactory() {
        final CachingConnectionFactory localhost = new CachingConnectionFactory(props.getHostname(), props.getPort());
        localhost.setUsername(props.getUsername());
        localhost.setPassword(props.getPassword());
        return localhost;
    }

}
