package com.example.receiver.flows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

import java.time.Instant;

@Configuration
public class Flow {

    @Bean
    public IntegrationFlow processMessage() {
        return IntegrationFlows.from("channelName")
                .handle(message -> {
                    System.out.println(Instant.now() + " - Received Message: " + message.getPayload());

                    try {
                        Thread.sleep(10000);
                    } catch (final InterruptedException e) {throw new RuntimeException(e);}

                    System.out.println(Instant.now() + " - Processed Message: " + message.getPayload());
                })
                .get();
    }

}

