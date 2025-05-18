package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.walkrecord.LocationMessage;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import com.example.PetApp.util.HaversineUtil;
import com.example.PetApp.util.SendNotificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final HaversineUtil haversineUtil;
    private final SendNotificationUtil sendNotificationUtil;


    public void sendLocation(LocationMessage locationMessage, String memberId) {
        WalkRecord walkRecord = walkRecordRepository.findById(locationMessage.getWalkRecordId())
                .orElseThrow(() -> new IllegalArgumentException("해당 산책 기록이 없습니다."));

        if (!(walkRecord.getDelegateWalkPost().getSelectedApplicantMemberId().equals(Long.valueOf(memberId)))) {
            throw new IllegalArgumentException("접근 권한 없음.");
        } else if (walkRecord.getWalkStatus() != WalkRecord.WalkStatus.START) {
            throw new IllegalArgumentException("start 권한 없음.");
        }
        Double locationLongitude = walkRecord.getDelegateWalkPost().getLocationLongitude();
        Double locationLatitude = walkRecord.getDelegateWalkPost().getLocationLatitude();
        Double walkerLongitude = locationMessage.getLongitude();
        Double walkerLatitude = locationMessage.getLatitude();

        double distanceInMeters = haversineUtil.calculateDistanceInMeters(locationLatitude, locationLongitude, walkerLatitude, walkerLongitude);
        if (distanceInMeters >= walkRecord.getDelegateWalkPost().getAllowedRadiusMeters()) {
            log.warn("대리산책자가 산책범위에 벗어남.");
            try {
                sendNotificationUtil.sendNotification(walkRecord.getDelegateWalkPost().getProfile().getMember(),
                        "위험! "+walkRecord.getMember().getName()+"님이 산책범위에 벗어났습니다. 현재 위치는 기준 지점에서 약 "
                                +distanceInMeters+"m 떨어져있습니다.");
                sendNotificationUtil.sendNotification(walkRecord.getMember(),
                        "위험! 산책범위에 벗어났습니다. 산책 범위에 들어가주세요.");
            } catch (JsonProcessingException e) {
                throw new RuntimeException("알림 보내는 과정에서 오류", e);
            }
        }

        String location = walkerLongitude + "," + walkerLatitude;
        stringRedisTemplate.opsForList().rightPush("walk:path:" + locationMessage.getWalkRecordId(), location);

        simpMessagingTemplate.convertAndSend(
                "/sub/walk-record/location/" + locationMessage.getWalkRecordId(),
                locationMessage);
    }
}
