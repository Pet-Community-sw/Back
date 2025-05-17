package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalkRecordServiceImp implements WalkRecordService{

    private final WalkRecordRepository walkRecordRepository;
    private final MemberRepository memberRepository;

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
        return null;
    }

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
    }

    @Override
    public ResponseEntity<?> updateFinishWalkRecord(Long walkRecordId, String email) {
        return null;
    }
}
