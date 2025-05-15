package com.example.PetApp.service.walkerpost;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.delegateWalkpost.*;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.util.SendNotificationUtil;
import com.example.PetApp.util.TimeAgoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class DelegateWalkPostServiceImp implements DelegateWalkPostService {

    private final DelegateWalkPostRepository delegateWalkPostRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final TimeAgoUtil timeAgoUtil;
    private final SendNotificationUtil sendNotificationUtil;

    @Transactional
    @Override
    public ResponseEntity<?> checkProfile(Long profileId) {
        log.info("checkProfile 요청 profileId : {}", profileId);
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("profile 없음.");
        }
        return ResponseEntity.ok().body("profile 있음.");
    }

    @Transactional
    @Override
    public ResponseEntity<?> createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId) {
        log.info("createDelegateWalkPost 요청 profileId : {}", profileId);
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("profile 없음.");
        }
        Optional<Profile> profile = profileRepository.findById(profileId);
        Optional<Profile> createPostProfile = profileRepository.findById(createDelegateWalkPostDto.getProfileId());
        if (!(createPostProfile.equals(profile))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청.");
        }
        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .title(createDelegateWalkPostDto.getTitle())
                .content(createDelegateWalkPostDto.getContent())
                .price(createDelegateWalkPostDto.getPrice())
                .locationLongitude(createDelegateWalkPostDto.getLocationLongitude())
                .locationLatitude(createDelegateWalkPostDto.getLocationLatitude())
                .allowedRadiusMeters(createDelegateWalkPostDto.getAllowedRadiusMeters())
                .scheduledTime(createDelegateWalkPostDto.getScheduledTime())
                .profile(createPostProfile.get())
                .requireProfile(createDelegateWalkPostDto.isRequireProfile())
                .build();
        DelegateWalkPost savedDelegateWalkPost = delegateWalkPostRepository.save(delegateWalkPost);
        return ResponseEntity.ok().body(Map.of("delegateWalkPostId", savedDelegateWalkPost.getDelegateWalkPostId()));

    }

    @Transactional
    @Override
    public ResponseEntity<?> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getDelegateWalkPostsByLocation 요청 memberId : {}", member.getMemberId());
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01);
        List<GetDelegateWalkPostsResponseDto> delegateWalkPostsResponseDtos = getGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
        return ResponseEntity.ok(delegateWalkPostsResponseDtos);
    }

    @Transactional
    @Override
    public ResponseEntity<?> getDelegateWalkPostsByPlace(Double longitude, Double latitude, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getDelegateWalkPostsByPlace 요청 memberId : {}", member.getMemberId());
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByPlace(longitude, latitude);
        List<GetDelegateWalkPostsResponseDto> delegateWalkPostsResponseDtos = getGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
        return ResponseEntity.ok(delegateWalkPostsResponseDtos);
    }

    @Transactional
    @Override
    public ResponseEntity<?> getDelegateWalkPost(Long delegateWalkPostId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getDelegateWalkPost 요청 memberId : {}", member.getMemberId());
        Optional<DelegateWalkPost> delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId);
        if (delegateWalkPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 개시글은 없습니다.");
        } else if (filter(delegateWalkPost.get(), member)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("profile 있어야함.");
        }
        DelegateWalkPost post=delegateWalkPost.get();
        GetPostResponseDto getPostResponseDto = GetPostResponseDto.builder()
                .delegateWalkPostId(post.getDelegateWalkPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .price(post.getPrice())
                .locationLongitude(post.getLocationLongitude())
                .locationLatitude(post.getLocationLatitude())
                .allowedRadiusMeters(post.getAllowedRadiusMeters())
                .scheduledTime(post.getScheduledTime())
                .petName(post.getProfile().getPetName())
                .petImageUrl(post.getProfile().getPetImageUrl())
                .petBreed(post.getProfile().getPetBreed())
                .extraInfo(post.getProfile().getExtraInfo())
                .applicantCount(post.getApplicants().size())
                .build();
        return ResponseEntity.ok(getPostResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("updateDelegateWalkPost 요청 delegateWalkPostId : {}, memberId : {}", delegateWalkPostId, member.getMemberId());
        Optional<DelegateWalkPost> delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId);
        if (delegateWalkPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 게시글은 없습니다.");
        } else if (!(delegateWalkPost.get().getProfile().getMember().equals(member))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한 없음.");
        }
        DelegateWalkPost post = delegateWalkPost.get();
        post.setTitle(updateDelegateWalkPostDto.getTitle());
        post.setContent(updateDelegateWalkPostDto.getContent());
        post.setPrice(updateDelegateWalkPostDto.getPrice());
        post.setAllowedRadiusMeters(updateDelegateWalkPostDto.getAllowedRedisMeters());
        post.setRequireProfile(updateDelegateWalkPostDto.isRequireProfile());
        post.setScheduledTime(updateDelegateWalkPostDto.getScheduledTime());

        return ResponseEntity.ok().body("수정 완료.");
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteDelegateWalkPost(Long delegateWalkPostId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("deleteDelegateWalkPost 요청 delegateWalkPostId : {}, memberId : {}", delegateWalkPostId, member.getMemberId());
        Optional<DelegateWalkPost> delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId);
        if (delegateWalkPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 게시글은 없습니다.");
        } else if (!(delegateWalkPost.get().getProfile().getMember().equals(member))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한 없음.");
        }
        delegateWalkPostRepository.deleteById(delegateWalkPostId);
        return ResponseEntity.ok().body("삭제 완료.");
    }

    @Transactional
    @Override
    public ResponseEntity<?> getApplicants(Long delegateWalkPostId, Long profileId) {
        log.info("getApplicants 요청 delegateWalkPostId : {}, profileId : {}", delegateWalkPostId, profileId);
        if (profileId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("profile 없음.");
        }
        Profile profile = profileRepository.findById(profileId).get();
        Optional<DelegateWalkPost> delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId);
        if (delegateWalkPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 게시글은 없습니다.");
        } else if (!(delegateWalkPost.get().getProfile().equals(profile))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        return ResponseEntity.ok(delegateWalkPost.get().getApplicants());
    }

    @Transactional
    @Override
    public ResponseEntity<?> applyToDelegateWalkPost(Long delegateWalkPostId,String content, String email) throws JsonProcessingException {
        Member member = memberRepository.findByEmail(email).get();
        log.info("applyToDelegateWalkPost 요청 delegateWalkPostId : {}, memberId : {}", delegateWalkPostId, member.getMemberId());
        Optional<DelegateWalkPost> delegateWalkPost = delegateWalkPostRepository.findById(delegateWalkPostId);
        if (delegateWalkPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 대리산책자 게시글은 없습니다.");
        } else if (filter(delegateWalkPost.get(), member)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("profile을 등록해야됨.");
        }else if ( delegateWalkPost.get().getApplicants().stream().
                anyMatch(applicant -> applicant.getMemberId().equals(member.getMemberId()))){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 신청한 회원입니다.");
        }
        delegateWalkPost.get().getApplicants().add(Applicant.builder()
                .memberId(member.getMemberId())
                .content(content)
                .build());
        String message = member.getName() + "님이 회원님의 대리산책자 게시글에 지원했습니다.";
        sendNotificationUtil.sendNotification(delegateWalkPost.get().getProfile().getMember(), member, message);
        return ResponseEntity.ok().body("신청 완료.");
    }


    @NotNull
    private List<GetDelegateWalkPostsResponseDto> getGetDelegateWalkPostsResponseDtos(Member member, List<DelegateWalkPost> delegateWalkPosts) {
        return delegateWalkPosts.stream()
                .map(delegateWalkPost -> GetDelegateWalkPostsResponseDto.builder()
                        .delegateWalkPostId(delegateWalkPost.getDelegateWalkPostId())
                        .title(delegateWalkPost.getTitle())
                        .price(delegateWalkPost.getPrice())
                        .locationLongitude(delegateWalkPost.getLocationLongitude())
                        .locationLatitude(delegateWalkPost.getLocationLatitude())
                        .scheduledTime(delegateWalkPost.getScheduledTime())
                        .filtering(filter(delegateWalkPost, member))
                        .applicantCount(delegateWalkPost.getApplicants().size())
                        .createdAt(timeAgoUtil.getTimeAgo(delegateWalkPost.getDelegateWalkPostTime()))
                        .build())
                .collect(Collectors.toList());
    }

    private static boolean filter(DelegateWalkPost delegateWalkPost, Member member) {
        if (delegateWalkPost.isRequireProfile()) {
            if (member.getProfile().size() != 0) {
                return false;
            }else
                return true;
        }else
            return false;
    }
}

