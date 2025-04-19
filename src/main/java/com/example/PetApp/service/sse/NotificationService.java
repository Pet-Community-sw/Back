package com.example.PetApp.service.sse;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.notification.NotificationListDto;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final ObjectMapper objectMapper;


    public ResponseEntity<?> getNotifications(Long profileId) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if ( profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        Set<String> keys = notificationRedisTemplate.keys("notifications:" + profileId + ":*");
        List<NotificationListDto> list = keys.stream()
                .map(key -> notificationRedisTemplate.opsForValue().get(key))
                .map(message -> {
                    try {
                        return objectMapper.readValue((String) message, NotificationListDto.class);
                        //readValue는 String,class로 기대를함. 그래서 명시적(String)으로 형변환.
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(list);
    }
}
