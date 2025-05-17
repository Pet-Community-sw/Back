package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.walkrecord.GetWalkRecordLocationResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordResponseDto;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
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

    @Transactional
    @Override
    public ResponseEntity<?> createWalkRecord(DelegateWalkPost delegateWalkPost) {
        log.info("createWalkRecord 요청");
        Optional<Member> member = memberRepository.findById(delegateWalkPost.getSelectedApplicantMemberId());
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 유저가 없습니다.");
        }
        WalkRecord walkRecord=WalkRecord.builder()
                .walkStatus(WalkRecord.WalkStatus.READY)
                .delegateWalkPost(delegateWalkPost)
                .member(member.get())
                .build();
        WalkRecord savedWalkRecord = walkRecordRepository.save(walkRecord);
        return ResponseEntity.ok().body(Map.of("walkRecordId", savedWalkRecord.getWalkRecordId()));
    }

    @Transactional
    @Override
    public ResponseEntity<?> getWalkRecord(Long walkRecordId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getWalkRecord 요청 walkRecordId : {}, memberId : {}", walkRecordId, member.getMemberId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(walkRecordId);
        if (walkRecord.isEmpty()) {
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
        return ResponseEntity.ok(walkRecordResponseDto);

    }

    @Transactional
    @Override//위치 값만 넘겨도 되려나?
    public ResponseEntity<?> getWalkRecordLocation(Long walkRecordId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getWalkRecordLocation 요청 walkRecordId : {}, memberId : {}", walkRecordId, member.getMemberId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(walkRecordId);
        if (walkRecord.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 게시글은 없습니다.");
        } else if (!(walkRecord.get().getDelegateWalkPost().getProfile().getMember().equals(member))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        String lastLocation = stringRedisTemplate.opsForList().index("walk:path:" + walkRecordId, -1);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        } else if (!(walkRecord.get().getDelegateWalkPost().getSelectedApplicantMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        walkRecord.get().setWalkStatus(WalkRecord.WalkStatus.START);
        walkRecord.get().setStartTime(LocalDateTime.now());//이때 /sub/walk-record/location/{walkRecordId}가 필요.
        return ResponseEntity.ok().body("start");
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateFinishWalkRecord(Long walkRecordId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("updateFinishWalkRecord 요청 walkRecordId : {}, memberId : {}",walkRecordId, member.getMemberId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(walkRecordId);
        if (walkRecord.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        } else if (!(walkRecord.get().getDelegateWalkPost().getSelectedApplicantMemberId().equals(member.getMemberId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        walkRecord.get().setWalkStatus(WalkRecord.WalkStatus.FINISH);
        walkRecord.get().setFinishTime(LocalDateTime.now());

        List<String> paths = stringRedisTemplate.opsForList().range("walk:path:" + walkRecordId, 0, -1);
        Double totalDistance = calculateTotalDistance(paths);
        walkRecord.get().setWalkDistance(totalDistance);
        walkRecord.get().setPathPoints(paths);//finish를 하고 후기를 작성할 수 있어야됨.
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
            double distanceInMeters = calculateDistanceInMeters(latitude1, longitude1, latitude2, longitude2);
            totalDistance += distanceInMeters;
        }
        return totalDistance;
    }

    //거리 계산 공식(Haversine)
    private double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public String getFormattedWalkDuration(LocalDateTime start, LocalDateTime finish) {
        Duration duration = Duration.between(start, finish);

        long seconds = duration.getSeconds();

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d시간 %d분 %d초", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, secs);
        } else {
            return String.format("%d초", secs);
        }

    }
}
