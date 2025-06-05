package com.example.PetApp.controller;

import com.example.PetApp.dto.notification.NotificationListDto;
import com.example.PetApp.service.sse.NotificationService;
import com.example.PetApp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")//sse 서버에서 클라이언트로 실시간으로 보내기 위함.
    public SseEmitter subscribe(@RequestParam String token) {
        return notificationService.subscribe(token);
    }

    @GetMapping
    public List<NotificationListDto> getNotifications(Authentication authentication) {
        return notificationService.getNotifications(AuthUtil.getEmail(authentication));
    }

}
