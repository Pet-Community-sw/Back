package com.example.PetApp.config.stomp;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub")//MessageBroker로 넘어감
                .setTaskScheduler(taskScheduler())
                .setHeartbeatValue(new long[]{3000L, 3000L});//하트비트설정

        config.setApplicationDestinationPrefixes("/pub");//@MessageMapping으로 넘어감
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    private ThreadPoolTaskScheduler taskScheduler() {//스케줄링 작업을 처리할 쓰레드 풀
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);//풀 크기를 설정 하나로 하트비트를 주기적으로 처리함.
        scheduler.setThreadNamePrefix("wss-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
                // 한 메시지 전송 제한 시간 (ms) – 기본 10초 → 여기선 15초로 증가
                .setSendTimeLimit(15 * 1000)

                // 전송할 메시지 버퍼 크기 제한 (byte) – 512KB
                .setSendBufferSizeLimit(512 * 1024)

                // 수신/전송 메시지 최대 크기 (byte) – 128KB, Dos 대비
                .setMessageSizeLimit(128 * 1024);
    }
}
