package com.example.receiver.configurations;

import org.springframework.amqp.core.*;
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
public class AmqpConfigurationSingleActiveConsumer {

    @Bean(name = "singleActiveConsumerQueue")
    Queue singleActiveConsumerQueue() {
        final Map<String, Object> args = new HashMap<>();
        args.put("x-queue-type", "quorum");         // Using quorum queues
        args.put("x-single-active-consumer", true); // ENABLE SINGLE ACTIVE CONSUMER
        return new Queue("sac.queue", true, false, false, args);
    }

    @Bean(name = "singleActiveConsumerBinding")
    Binding singleActiveConsumerBinding(@Qualifier(value = "singleActiveConsumerQueue") Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("routingkey.single_active_consumer").noargs();
    }

    @Bean("singleActiveConsumerInboundAdapter")
    AmqpInboundChannelAdapterSMLCSpec singleActiveConsumerInboundAdapter(final ConnectionFactory connectionFactory,
                                                                   @Qualifier("singleActiveConsumerQueue") final Queue queue, final RetryTemplate retryTemplate) {
        return ConfigurationUtil.buildAmqpInboundAdapter(connectionFactory, queue, retryTemplate, false);
    }

    @Bean
    IntegrationFlow singleActiveConsumerInboundFlow(@Qualifier("singleActiveConsumerInboundAdapter") AmqpInboundChannelAdapterSMLCSpec inboundAdapter) {
        return ConfigurationUtil.buildEventIntegrationFlowUsingHeaderRoutingKey(inboundAdapter,
                Map.of("routingkey.single_active_consumer", "channelName"));
    }

}
