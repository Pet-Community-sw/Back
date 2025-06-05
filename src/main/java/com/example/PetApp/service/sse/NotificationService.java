package com.example.PetApp.service.sse;

import com.example.PetApp.dto.notification.NotificationListDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
public interface NotificationService {

    List<NotificationListDto> getNotifications(String email);

    SseEmitter subscribe(String token);
}
