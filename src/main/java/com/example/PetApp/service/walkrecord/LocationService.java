package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.walkrecord.LocationMessage;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {//예외 처리해야됨.

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final WalkRecordRepository walkRecordRepository;

    public void sendLocation(LocationMessage locationMessage, String memberId) {
        WalkRecord walkRecord = walkRecordRepository.findById(locationMessage.getWalkRecordId())
                .orElseThrow(() -> new IllegalArgumentException("해당 산책 기록이 없습니다."));

        if (!(walkRecord.getDelegateWalkPost().getSelectedApplicantMemberId().equals(Long.valueOf(memberId)))) {
            throw new IllegalArgumentException("접근 권한 없음.");
        } else if (walkRecord.getWalkStatus() != WalkRecord.WalkStatus.START) {
            throw new IllegalArgumentException("start 권한 없음.");
        }

        String location = locationMessage.getLongitude() + "," + locationMessage.getLatitude();
        stringRedisTemplate.opsForList().rightPush("walk:path:" + locationMessage.getWalkRecordId(), location);

        simpMessagingTemplate.convertAndSend(
                "/sub/walk-record/location/" + locationMessage.getWalkRecordId(),
                locationMessage);

    }
}
