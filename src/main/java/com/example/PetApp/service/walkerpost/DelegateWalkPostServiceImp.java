package com.example.PetApp.service.walkerpost;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.delegateWalkpost.CreateDelegateWalkPostDto;
import com.example.PetApp.dto.delegateWalkpost.GetDelegateWalkPostsResponseDto;
import com.example.PetApp.dto.delegateWalkpost.GetPostResponseDto;
import com.example.PetApp.dto.delegateWalkpost.UpdateDelegateWalkPostDto;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.util.TimeAgoUtil;
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
        delegateWalkPost.get().setTitle(updateDelegateWalkPostDto.getTitle());
        delegateWalkPost.get().setContent(updateDelegateWalkPostDto.getContent());
        delegateWalkPost.get().setPrice(updateDelegateWalkPostDto.getPrice());
        delegateWalkPost.get().setAllowedRadiusMeters(updateDelegateWalkPostDto.getAllowedRedisMeters());
        delegateWalkPost.get().setRequireProfile(updateDelegateWalkPostDto.isRequireProfile());
        delegateWalkPost.get().setScheduledTime(updateDelegateWalkPostDto.getScheduledTime());

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
    public ResponseEntity<?> getApplicants(Long delegateWalkPostId, String email) {
        return null;
    }

    @Transactional
    @Override
    public ResponseEntity<?> applyToDelegateWalkPost(Long delegateWalkPostId, String email) {
        return null;
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

