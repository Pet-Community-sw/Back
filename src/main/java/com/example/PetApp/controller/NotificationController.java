package com.example.PetApp.controller;

import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.sse.NotificationService;
import com.example.PetApp.service.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final SseEmitterManager sseEmitterManager;
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")//sse 서버에서 클라이언트로 실시간으로 보내기 위함.
    public SseEmitter subscribe(@RequestParam String token) {
        return sseEmitterManager.subscribe(token);
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return notificationService.getNotifications(email);
    }
}
