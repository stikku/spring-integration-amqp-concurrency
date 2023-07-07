package com.example.receiver.configurations;

import com.example.receiver.configurations.properties.AMQPProperties;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@Scope("singleton")
public class AmqpConfiguration {

    @Autowired
    private AMQPProperties props;

    @Bean
    Exchange exchange() {
        return new TopicExchange(props.getExchange(), true, false);
    }

    @Bean
    RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
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

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(1000);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(1);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
