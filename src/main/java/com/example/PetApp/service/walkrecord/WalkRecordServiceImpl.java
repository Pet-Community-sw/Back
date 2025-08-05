package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.walkrecord.CreateWalkRecordResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordLocationResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordResponseDto;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.WalkRecordMapper;
import com.example.PetApp.query.MemberQueryService;
import com.example.PetApp.query.WalkRecordQueryService;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import com.example.PetApp.util.DistanceUtil;
import com.example.PetApp.util.SendNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalkRecordServiceImpl implements WalkRecordService{

    private final WalkRecordRepository walkRecordRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final SendNotificationUtil sendNotificationUtil;
    private final MemberQueryService memberQueryService;
    private final WalkRecordQueryService walkRecordQueryService;

    @Transactional
    @Override
    public CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost) {
        log.info("createWalkRecord 요청");
        Member member = memberQueryService.findByMember(delegateWalkPost.getSelectedApplicantMemberId());
        WalkRecord walkRecord = WalkRecordMapper.toEntity(delegateWalkPost, member);
        WalkRecord savedWalkRecord = walkRecordRepository.save(walkRecord);
        sendNotificationUtil.sendNotification(member, "산책 권한이 부여 되었습니다.");
        return new CreateWalkRecordResponseDto(savedWalkRecord.getWalkRecordId());
    }

    @Transactional(readOnly = true)
    @Override
    public GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, String email) {
        log.info("getWalkRecord 요청 walkRecordId : {}, email : {}", walkRecordId, email);
        WalkRecord walkRecord = walkRecordQueryService.findByWalkRecord(walkRecordId);
        return WalkRecordMapper.toGetWalkRecordResponseDto(walkRecord);
    }

    @Transactional(readOnly = true)
    @Override
    public GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, String email) {
        log.info("getWalkRecordLocation 요청 walkRecordId : {}, email : {}", walkRecordId, email);
        Member member = memberQueryService.findByMember(email);
        WalkRecord walkRecord = walkRecordQueryService.findByWalkRecord(walkRecordId);
        validateMember(walkRecord.getDelegateWalkPost().getProfile().getMember().equals(member));
        String lastLocation = stringRedisTemplate.opsForList().index("walk:path:" + walkRecordId, -1);
        return new GetWalkRecordLocationResponseDto(lastLocation);
    }

    @Transactional
    @Override
    public void updateStartWalkRecord(Long walkRecordId, String email) {
        log.info("updateStartWalkRecord 요청 walkRecordId : {}, email : {}",walkRecordId, email);
        Member member = memberQueryService.findByMember(email);
        WalkRecord walkRecord = walkRecordQueryService.findByWalkRecord(walkRecordId);
        validateMember(walkRecord.getDelegateWalkPost().getSelectedApplicantMemberId().equals(member.getMemberId()));
        walkRecord.setWalkStatus(WalkRecord.WalkStatus.START);
        walkRecord.setStartTime(LocalDateTime.now());
        sendNotificationUtil.sendNotification(walkRecord.getDelegateWalkPost().getProfile().getMember(),
                walkRecord.getMember().getName()+"님이 산책을 시작하였습니다.");
    }

    @Transactional//분리를 어떻게 시키면 졸을까
    @Override
    public void updateFinishWalkRecord(Long walkRecordId, String email) {
        log.info("updateFinishWalkRecord 요청 walkRecordId : {}, email : {}",walkRecordId, email);
        Member member = memberQueryService.findByMember(email);
        WalkRecord walkRecord = walkRecordQueryService.findByWalkRecord(walkRecordId);
        validateMember(walkRecord.getDelegateWalkPost().getSelectedApplicantMemberId().equals(member.getMemberId()));
        walkRecord.setWalkStatus(WalkRecord.WalkStatus.FINISH);
        walkRecord.setFinishTime(LocalDateTime.now());

        updateWalkRecordPathData(walkRecordId, walkRecord);
        sendNotificationUtil.sendNotification(walkRecord.getDelegateWalkPost().getProfile().getMember(),
                walkRecord.getMember().getName()+"님이 산책을 마쳤습니다. 후기를 작성해주세요.");
    }

    private void updateWalkRecordPathData(Long walkRecordId, WalkRecord walkRecord) {
        List<String> paths = stringRedisTemplate.opsForList().range("walk:path:" + walkRecordId, 0, -1);
        Double totalDistance = DistanceUtil.calculateTotalDistance(paths);
        walkRecord.setWalkDistance(totalDistance);
        walkRecord.setPathPoints(paths);
        stringRedisTemplate.delete("walk:path:" + walkRecordId);
    }

    private static void validateMember(boolean walkRecord) {
        if (!walkRecord) {
            throw new ForbiddenException("권한 없음.");
        }
    }
}
