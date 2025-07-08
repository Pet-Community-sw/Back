package com.example.PetApp.config.stomp.strategy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompCommand;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class StompStrategyConfig {

    @Bean
    public Map<StompCommand, StompCommandStrategy> strategyMap(
            ConnectStrategy connectStrategy,
            SubscribeStrategy subscribeStrategy,
            UnsubscribeStrategy unsubscribeStrategy) {

        Map<StompCommand, StompCommandStrategy> map = new EnumMap<>(StompCommand.class);
        map.put(StompCommand.CONNECT, connectStrategy);
        map.put(StompCommand.SUBSCRIBE, subscribeStrategy);
        map.put(StompCommand.UNSUBSCRIBE, unsubscribeStrategy);

        return map;
    }
}
