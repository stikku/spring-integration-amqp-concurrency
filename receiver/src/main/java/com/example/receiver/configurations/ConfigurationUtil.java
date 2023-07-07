package com.example.receiver.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.AmqpMessageHeaderAccessor;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.dsl.AmqpInboundChannelAdapterSMLCSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

public class ConfigurationUtil {

    public static AmqpInboundChannelAdapterSMLCSpec buildAmqpInboundAdapter(final ConnectionFactory connectionFactory,
                                                                      final Queue queue,
                                                                      final RetryTemplate retryTemplate,
                                                                      final boolean exclusive) {
        final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        listenerContainer.setQueues(queue);
        listenerContainer.setPrefetchCount(5);
//        listenerContainer.setPrefetchCount(250);
        listenerContainer.setExclusive(exclusive);

        // Shutdown should wait until done with all messages in the LOCAL queue.
        // Note that Long.MAX_VALUE does not work as intended.
        listenerContainer.setShutdownTimeout(Integer.MAX_VALUE);

        return Amqp.inboundAdapter(listenerContainer)
                .retryTemplate(retryTemplate);
    }

    public static IntegrationFlow buildEventIntegrationFlowUsingHeaderRoutingKey(final AmqpInboundChannelAdapterSMLCSpec inboundAdapter,
                                                                           final Map<String, String> channelMappings) {
        return IntegrationFlows
                .from(inboundAdapter)
                .log(LoggingHandler.Level.INFO, message -> {
                    final AmqpMessageHeaderAccessor accessor = AmqpMessageHeaderAccessor.wrap(message);
                    return "Received event with id " + accessor.getMessageId() +
                            ", key " + message.getHeaders().get("routingKey", String.class) +
                            " and payload " + message.getPayload().toString();
                })

                .route(Message.class,
                        message -> message.getHeaders().get("routingKey", String.class),
                        routerSpec -> channelMappings
                                .forEach(routerSpec::channelMapping))
                .get();
    }
}
