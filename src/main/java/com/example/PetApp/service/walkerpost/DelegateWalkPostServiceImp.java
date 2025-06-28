package com.example.PetApp.service.walkerpost;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.delegateWalkpost.*;
import com.example.PetApp.dto.memberchat.CreateMemberChatRoomResponseDto;
import com.example.PetApp.dto.walkrecord.CreateWalkRecordResponseDto;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.DelegateWalkPostMapper;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.service.memberchatRoom.MemberChatRoomService;
import com.example.PetApp.service.walkrecord.WalkRecordService;
import com.example.PetApp.util.SendNotificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Service
public class DelegateWalkPostServiceImp implements DelegateWalkPostService {

    private final DelegateWalkPostRepository delegateWalkPostRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomService memberChatRoomService;
    private final WalkRecordService walkRecordService;
    private final SendNotificationUtil sendNotificationUtil;


    @Transactional
    @Override
    public CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId) {
        log.info("createDelegateWalkPost 요청 profileId : {}", profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 등록해주세요."));
        DelegateWalkPost delegateWalkPost = DelegateWalkPostMapper.toEntity(createDelegateWalkPostDto, profile);

        DelegateWalkPost savedDelegateWalkPost = delegateWalkPostRepository.save(delegateWalkPost);
        return new CreateDelegateWalkPostResponseDto(savedDelegateWalkPost.getDelegateWalkPostId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email) {
        log.info("getDelegateWalkPostsByLocation 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01);
        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, String email) {
        log.info("getDelegateWalkPostsByPlace 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByPlace(longitude, latitude);

        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional(readOnly = true)
    @Override
    public GetPostResponseDto getDelegateWalkPost(Long delegateWalkPostId, String email) {
        log.info("getDelegateWalkPost 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        DelegateWalkPost delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId)
                .orElseThrow(() -> new NotFoundException("해당 대리산책자 게시글은 없습니다."));
         if (DelegateWalkPostMapper.filter(delegateWalkPost, member)) {
             throw new ForbiddenException("프로필 등록해주세요.");
        }
        return DelegateWalkPostMapper.toGetPostResponseDto(delegateWalkPost);
    }

    @Override
    public ResponseEntity<?> checkProfile(Long profileId) {
        log.info("checkProfile 요청 profileId : {}", profileId);
        if (profileId == null) {
            return ResponseEntity.ok().body("profile 없음.");
        }
        return ResponseEntity.ok().body("profile 있음.");
    }

    @Transactional
    @Override
    public CreateMemberChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, String email) {
        log.info("selectApplicant 요청 delegateWalkPostId : {}, email : {}", delegateWalkPostId, email);
        Member member = memberRepository.findByEmail(email).get();
        Member applicantMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 지원자는 없습니다."));
        DelegateWalkPost delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId)
                .orElseThrow(() -> new NotFoundException("해당 대리산책자 게시글은 없습니다."));
        if (!(delegateWalkPost.getProfile().getMember().equals(member))) {
            throw new ForbiddenException("권한 없음.");
        } else if (delegateWalkPost.getApplicants().stream().noneMatch(applicant -> applicant.getMemberId().equals(memberId))) {
            throw new ConflictException("해당 지원자는 없습니다.");
        }
        delegateWalkPost.setStatus(DelegateWalkPost.DelegateWalkStatus.COMPLETED);
        delegateWalkPost.setSelectedApplicantMemberId(memberId);
        //켈린더에 넣는 로직필요.
        sendNotification(applicantMember, "대리산책자 지원에 선정되었습니다.");
        return memberChatRoomService.createMemberChatRoom(member, memberRepository.findById(memberId).get());
    }

    @Transactional//산책 허가.
    @Override
    public CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId)
                .orElseThrow(() -> new NotFoundException("해당 대리산책자 게시글은 없습니다."));
        if (!(delegateWalkPost.getProfile().getProfileId().equals(profileId))) {
            throw new ForbiddenException("권한 없음.");
        }
        delegateWalkPost.setStartAuthorized(true);//산책 start 허가.
        return walkRecordService.createWalkRecord(delegateWalkPost);
    }

    @Transactional
    @Override
    public void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email) {
        log.info("updateDelegateWalkPost 요청 delegateWalkPostId : {}, memberId : {}", delegateWalkPostId, email);
        Member member = memberRepository.findByEmail(email).get();
        DelegateWalkPost delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId)
                .orElseThrow(() -> new NotFoundException("해당 대리산책자 게시글은 없습니다."));
         if (!(delegateWalkPost.getProfile().getMember().equals(member))) {
             throw new ForbiddenException("수정 권한 없음.");
        }
        DelegateWalkPostMapper.updateDelegateWalkPost(updateDelegateWalkPostDto, delegateWalkPost);
    }

    @Transactional
    @Override
    public void deleteDelegateWalkPost(Long delegateWalkPostId, String email) {
        log.info("deleteDelegateWalkPost 요청 delegateWalkPostId : {}, email : {}", delegateWalkPostId, email);
        Member member = memberRepository.findByEmail(email).get();
        DelegateWalkPost delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId)
                .orElseThrow(() -> new NotFoundException("해당 대리산책자 게시글은 없습니다."));
        if (!(delegateWalkPost.getProfile().getMember().equals(member))) {
             throw new ForbiddenException("삭제 권한 없음.");
        }
        delegateWalkPostRepository.deleteById(delegateWalkPostId);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId) {
        log.info("getApplicants 요청 delegateWalkPostId : {}, profileId : {}", delegateWalkPostId, profileId);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ForbiddenException("프로필 등록해주세요."));
        DelegateWalkPost delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId)
                .orElseThrow(() -> new NotFoundException("해당 대리산책자 게시글은 없습니다."));
        if (!(delegateWalkPost.getProfile().equals(profile))) {
            throw new ForbiddenException("권한 없음.");
        }
        return delegateWalkPost.getApplicants();
    }

    @Transactional
    @Override
    public ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email) {
        log.info("applyToDelegateWalkPost 요청 delegateWalkPostId : {}, email : {}", delegateWalkPostId, email);
        Member member = memberRepository.findByEmail(email).get();
        DelegateWalkPost delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId)
                .orElseThrow(() -> new NotFoundException("해당 대리산책자 게시글은 없습니다."));
        if (DelegateWalkPostMapper.filter(delegateWalkPost, member)) {
            throw new ForbiddenException("프로필 등록해주세요.");
        } else if (delegateWalkPost.getApplicants().stream().
                anyMatch(applicant -> applicant.getMemberId().equals(member.getMemberId()))) {
            throw new ConflictException("이미 신청한 회원입니다.");
        } else if (delegateWalkPost.getStatus() == DelegateWalkPost.DelegateWalkStatus.COMPLETED) {
            throw new ConflictException("모집 완료 게시글입니다.");
        }
        delegateWalkPost.getApplicants().add(Applicant.builder()
                .memberId(member.getMemberId())
                .content(content)
                .build());
        sendToDelegateWalkPostNotification(member, delegateWalkPost);
        return new ApplyToDelegateWalkPostResponseDto(member.getMemberId());
    }




    private void sendToDelegateWalkPostNotification(Member member, DelegateWalkPost delegateWalkPost) {
        String message = member.getName() + "님이 회원님의 대리산책자 게시글에 지원했습니다.";
        sendNotification(delegateWalkPost.getProfile().getMember(), message);

    }

    private void sendNotification(Member member, String message) {
            sendNotificationUtil.sendNotification(member, message);
    }
}

