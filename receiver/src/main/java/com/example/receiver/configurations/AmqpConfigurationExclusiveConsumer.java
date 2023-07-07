package com.example.receiver.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.amqp.dsl.AmqpInboundChannelAdapterSMLCSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Scope("singleton")
public class AmqpConfigurationExclusiveConsumer {

    @Bean(name = "exclusiveConsumerQueue")
    Queue exclusiveConsumerQueue() {
        final Map<String, Object> args = new HashMap<>();
        return new Queue("exclusive.consumer.queue", true, false, false, args);
    }

    @Bean(name = "exclusiveConsumerBinding")
    Binding exclusiveConsumerBinding(@Qualifier(value = "exclusiveConsumerQueue") Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("routingkey.exclusive_consumer").noargs();
    }

    @Bean("exclusiveConsumerInboundAdapter")
    AmqpInboundChannelAdapterSMLCSpec exclusiveConsumerInboundAdapter(final ConnectionFactory connectionFactory,
                                                                   @Qualifier("exclusiveConsumerQueue") final Queue queue, final RetryTemplate retryTemplate) {
        // Set the consumer to exclusive
        return ConfigurationUtil.buildAmqpInboundAdapter(connectionFactory, queue, retryTemplate, true);
    }

    @Bean
    IntegrationFlow exclusiveConsumerInboundFlow(@Qualifier("exclusiveConsumerInboundAdapter") AmqpInboundChannelAdapterSMLCSpec inboundAdapter) {
        return ConfigurationUtil.buildEventIntegrationFlowUsingHeaderRoutingKey(inboundAdapter,
                Map.of("routingkey.exclusive_consumer", "channelName"));
    }

}
