package com.example.PetApp.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class CreatedAtUtil {
    public static String createdAt(LocalDateTime start, LocalDateTime finish) {
        Duration duration = Duration.between(start, finish);

        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        String result;
        if (hours > 0) {
            result = String.format("%d시간 %d분 %d초", hours, minutes, secs);
        } else if (minutes > 0) {
            result = String.format("%d분 %d초", minutes, secs);
        } else {
            result = String.format("%d초", secs);
        }
        return result;
    }
}
