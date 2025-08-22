package com.example.PetApp.dto.notification;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationListDto {

    private String message;

    private LocalDateTime notificationTime;

    private String createdAt;

    public NotificationListDto(String message, LocalDateTime notificationTime) {
        this.message = message;
        this.notificationTime = notificationTime;
    }
}
