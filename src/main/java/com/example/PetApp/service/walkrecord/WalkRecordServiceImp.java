package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.walkrecord.GetWalkRecordLocationResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordResponseDto;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import com.example.PetApp.util.HaversineUtil;
import com.example.PetApp.util.SendNotificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalkRecordServiceImp implements WalkRecordService{

    private final WalkRecordRepository walkRecordRepository;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final SendNotificationUtil sendNotificationUtil;
    private final HaversineUtil haversineUtil;

    @Transactional
    @Override
    public ResponseEntity<?> createWalkRecord(DelegateWalkPost delegateWalkPost) {
        log.info("createWalkRecord 요청");
        Optional<Member> member = memberRepository.findById(delegateWalkPost.getSelectedApplicantMemberId());
        if (member.isEmpty()) {
            log.warn("해당 대리산책자 유저가 없습니다. id = {}", delegateWalkPost.getSelectedApplicantMemberId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 유저가 없습니다.");
        }
        WalkRecord walkRecord=WalkRecord.builder()
                .walkStatus(WalkRecord.WalkStatus.READY)
                .delegateWalkPost(delegateWalkPost)
                .member(member.get())
                .build();
        WalkRecord savedWalkRecord = walkRecordRepository.save(walkRecord);
        try {
            sendNotificationUtil.sendNotification(member.get(), "산책 권한이 부여 되었습니다.");
            log.info("산책 권한 부여 알림 전송 완료");
        } catch (JsonProcessingException e) {
            log.error("산책 권한 알림 전송 실패", e);
            throw new RuntimeException("알림 보내는 도중 예외 발생",e);
        }
        return ResponseEntity.ok().body(Map.of("walkRecordId", savedWalkRecord.getWalkRecordId()));
    }

    @Transactional
    @Override
    public ResponseEntity<?> getWalkRecord(Long walkRecordId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getWalkRecord 요청 walkRecordId : {}, memberId : {}", walkRecordId, member.getMemberId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(walkRecordId);
        if (walkRecord.isEmpty()) {
            log.warn("해당 대리산책자 게시글은 없습니다. id = {}", walkRecordId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 게시글은 없습니다.");
        }
        WalkRecord record = walkRecord.get();
        GetWalkRecordResponseDto walkRecordResponseDto=GetWalkRecordResponseDto.builder()
                .walkRecordId(record.getWalkRecordId())
                .startTime(record.getStartTime())
                .finishTime(record.getFinishTime())
                .WalkTime(getFormattedWalkDuration(record.getStartTime(),record.getFinishTime()))
                .walkDistance(record.getWalkDistance())
                .pathPoints(record.getPathPoints())
                .build();
        log.info("산책 기록 반환: {}", walkRecordResponseDto);
        return ResponseEntity.ok(walkRecordResponseDto);

    }

    @Transactional
    @Override
    public ResponseEntity<?> getWalkRecordLocation(Long walkRecordId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getWalkRecordLocation 요청 walkRecordId : {}, memberId : {}", walkRecordId, member.getMemberId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(walkRecordId);
        if (walkRecord.isEmpty()) {
            log.warn("산책기록 없음: walkRecordId = {}", walkRecordId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 게시글은 없습니다.");
        } else if (!(walkRecord.get().getDelegateWalkPost().getProfile().getMember().equals(member))) {
            log.warn("위치 조회 권한 없음: memberId = {}, 게시글 작성자 = {}",
                    member.getMemberId(), walkRecord.get().getDelegateWalkPost().getProfile().getMember().getMemberId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        String lastLocation = stringRedisTemplate.opsForList().index("walk:path:" + walkRecordId, -1);
        log.info("마지막 위치 정보: {}", lastLocation);
        GetWalkRecordLocationResponseDto getWalkRecordLocationResponseDto = GetWalkRecordLocationResponseDto.builder()
                .lastLocation(lastLocation)
                .build();
        return ResponseEntity.ok(getWalkRecordLocationResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateStartWalkRecord(Long walkRecordId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("updateStartWalkRecord 요청 walkRecordId : {}, memberId : {}",walkRecordId, member.getMemberId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(walkRecordId);
        if (walkRecord.isEmpty()) {
            log.warn("산책기록 없음: walkRecordId = {}", walkRecordId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        } else if (!(walkRecord.get().getDelegateWalkPost().getSelectedApplicantMemberId().equals(member.getMemberId()))) {
            log.warn("산책 시작 권한 없음: memberId = {}, selectedApplicant = {}",
                    member.getMemberId(), walkRecord.get().getDelegateWalkPost().getSelectedApplicantMemberId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        walkRecord.get().setWalkStatus(WalkRecord.WalkStatus.START);
        walkRecord.get().setStartTime(LocalDateTime.now());
        log.info("산책 시작: walkRecordId = {}, memberId = {}", walkRecordId, member.getMemberId());
        try {
            sendNotificationUtil.sendNotification(walkRecord.get().getDelegateWalkPost().getProfile().getMember(),
                    walkRecord.get().getMember().getName()+"님이 산책을 시작하였습니다.");
            log.info("산책 시작 알림 전송 완료");
        } catch (JsonProcessingException e) {
            log.error("산책 시작 알림 전송 실패", e);
            throw new RuntimeException("알림 보내는 도중 예외 발생",e);
        }
        return ResponseEntity.ok().body("start");
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateFinishWalkRecord(Long walkRecordId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("updateFinishWalkRecord 요청 walkRecordId : {}, memberId : {}",walkRecordId, member.getMemberId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(walkRecordId);
        if (walkRecord.isEmpty()) {
            log.warn("산책기록 없음: walkRecordId = {}", walkRecordId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        } else if (!(walkRecord.get().getDelegateWalkPost().getSelectedApplicantMemberId().equals(member.getMemberId()))) {
            log.warn("산책 종료 권한 없음: memberId = {}, selectedApplicant = {}",
                    member.getMemberId(), walkRecord.get().getDelegateWalkPost().getSelectedApplicantMemberId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        walkRecord.get().setWalkStatus(WalkRecord.WalkStatus.FINISH);
        walkRecord.get().setFinishTime(LocalDateTime.now());

        List<String> paths = stringRedisTemplate.opsForList().range("walk:path:" + walkRecordId, 0, -1);
        Double totalDistance = calculateTotalDistance(paths);
        walkRecord.get().setWalkDistance(totalDistance);
        walkRecord.get().setPathPoints(paths);
        stringRedisTemplate.delete("walk:path:" + walkRecordId);
        log.info("Redis 경로 데이터 삭제 완료: walk:path:{}", walkRecordId);
        try {
            sendNotificationUtil.sendNotification(walkRecord.get().getDelegateWalkPost().getProfile().getMember(),
                    walkRecord.get().getMember().getName()+"님이 산책을 마쳤습니다. 후기를 작성해주세요.");
            log.info("산책 종료 알림 전송 완료");
        } catch (JsonProcessingException e) {
            log.error("산책 종료 알림 전송 실패", e);
            throw new RuntimeException("알림 보내는 도중 예외 발생",e);
        }
        return ResponseEntity.ok().body("finish");
    }

    private Double calculateTotalDistance(List<String> list) {
        double totalDistance=0.0;
        for (int i = 1; i < list.size(); i++) {
            String path1 = list.get(i - 1);
            String path2 = list.get(i);

            String[] arr1 = path1.split(",");
            String[] arr2 = path2.split(",");

            double longitude1 = Double.parseDouble(arr1[0]);
            double latitude1 = Double.parseDouble(arr1[1]);
            double longitude2 = Double.parseDouble(arr2[0]);
            double latitude2 = Double.parseDouble(arr2[1]);
            double distanceInMeters = haversineUtil.calculateDistanceInMeters(latitude1, longitude1, latitude2, longitude2);
            totalDistance += distanceInMeters;
        }
        log.info("총 산책 거리 계산 완료: {} m", totalDistance);
        return totalDistance;
    }

    public String getFormattedWalkDuration(LocalDateTime start, LocalDateTime finish) {
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
        log.debug("산책 시간 포맷팅 결과: {}", result);
        return result;
    }
}
