package com.example.PetApp.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationListDto {

    private String message;

    private LocalDateTime notificationTime;

    private String createdAt;

    public NotificationListDto(String message, LocalDateTime notificationTime) {
        this.message = message;
        this.notificationTime = notificationTime;
    }
}
