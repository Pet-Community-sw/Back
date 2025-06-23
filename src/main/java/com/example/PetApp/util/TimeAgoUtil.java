package com.example.PetApp.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeAgoUtil {
    public static String getTimeAgo(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(localDateTime, now);
        long hours = ChronoUnit.HOURS.between(localDateTime, now);
        long days = ChronoUnit.DAYS.between(localDateTime, now);

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
    }

}
