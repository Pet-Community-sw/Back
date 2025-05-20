package com.example.PetApp.service.review;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.Review;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.review.*;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.ReviewRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.PetApp.domain.Review.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImp implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final WalkRecordRepository walkRecordRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public ResponseEntity<?> createReview(CreateReviewDto createReviewDto, String email) {
        log.info("createReview 요청 walkRecord : {}", createReviewDto.getWalkRecordId());
        Optional<WalkRecord> walkRecord = walkRecordRepository.findById(createReviewDto.getWalkRecordId());
        Member member = memberRepository.findByEmail(email).get();
        Profile profile = profileRepository.findById(createReviewDto.getProfileId()).get();
        if (walkRecord.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        } else if (walkRecord.get().getWalkStatus() != WalkRecord.WalkStatus.FINISH) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("산책을 다해야 후기를 작성할 수 있습니다.");
        } else if (!(walkRecord.get().getDelegateWalkPost().getProfile().getProfileId().equals(createReviewDto.getProfileId())
                && walkRecord.get().getMember().getMemberId().equals(createReviewDto.getMemberId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        } else if (!(profile.getMember().equals(member) || walkRecord.get().getMember().equals(member))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음.");
        }
        Review review= builder()
                .member(walkRecord.get().getMember())
                .profile(profile)
                .walkRecord(walkRecord.get())
                .title(createReviewDto.getTitle())
                .content(createReviewDto.getContent())
                .rating(createReviewDto.getRating())
                .reviewType(createReviewDto.getReviewType())
                .build();
        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.ok().body(Map.of("reviewId", savedReview.getReviewId()));
    }

    @Transactional
    @Override
    public ResponseEntity<?> getReviewListByMember(Long memberId, String email) {
        log.info("getReviewListByMember 요청 memberId : {}", memberId);
        Member ownerMember = memberRepository.findByEmail(email).get();
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저가 없습니다.");
        }
        List<Review> reviewList = reviewRepository.findAllByMemberAndReviewType(member.get(), ReviewType.PROFILE_TO_MEMBER);
        List<GetReviewListByMember> getReviewListByMembers = reviewList.stream()
                .map(review -> GetReviewListByMember.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getProfile().getProfileId())
                        .userName(review.getProfile().getPetName())
                        .userImageUrl(review.getProfile().getPetImageUrl())
                        .title(review.getTitle())
                        .rating(review.getRating())
                        .reviewTime(review.getReviewTime())
                        .isOwner(checkOwner(ownerMember,member.get()))
                        .build()
        ).collect(Collectors.toList());
        //이렇게 하면 안될것같은데
        GetReviewListByMemberResponseDto reviewMemberList=GetReviewListByMemberResponseDto.builder()
                .userId(ownerMember.getMemberId())
                .userName(ownerMember.getName())
                .userImageUrl(ownerMember.getMemberImageUrl())
                .averageRating(reviewList.stream().mapToInt(Review::getRating).average().orElse(0.0))
                .reviewCount(reviewList.size())
                .reviewList(getReviewListByMembers)
                .build();

        return ResponseEntity.ok(reviewMemberList);
    }

    @Transactional
    @Override
    public ResponseEntity<?> getReviewListByProfile(Long profileId, String email) {
        log.info("getReviewListByProfile 요청 profileId : {}", profileId);
        Member member = memberRepository.findByEmail(email).get();
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 프로필이 없습니다.");
        }
        Profile ownerProfile = profile.get();
        List<Review> reviewList = reviewRepository.findAllByProfileAndReviewType(ownerProfile, ReviewType.MEMBER_TO_PROFILE);
        List<GetReviewListByMember> getReviewListByMembers = reviewList.stream().map(review ->
                GetReviewListByMember.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getMember().getMemberId())
                        .userName(review.getMember().getName())
                        .userImageUrl(review.getMember().getMemberImageUrl())
                        .title(review.getTitle())
                        .rating(review.getRating())
                        .reviewTime(review.getReviewTime())
                        .isOwner(checkOwner(member, ownerProfile.getMember()))
                        .build()
        ).collect(Collectors.toList());

        GetReviewListByMemberResponseDto reviewMemberList = GetReviewListByMemberResponseDto.builder()
                .userId(ownerProfile.getProfileId())
                .userName(ownerProfile.getPetName())
                .userImageUrl(ownerProfile.getPetImageUrl())
                .averageRating(reviewList.stream().mapToInt(Review::getRating).average().orElse(0.0))
                .reviewCount(reviewList.size())
                .reviewList(getReviewListByMembers)
                .build();


        return ResponseEntity.ok(reviewMemberList);

    }


    @Transactional
    @Override
    public ResponseEntity<?> getReview(Long reviewId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getReview 요청 reviewId : {}, memberId : {}", reviewId, member.getMemberId());
        Optional<Review> review = reviewRepository.findById(reviewId);
        if (review.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        }
        Review mainReview = review.get();
        GetReviewResponseDto getReviewResponseDto=GetReviewResponseDto.builder()
                .reviewId(mainReview.getReviewId())
                .title(mainReview.getTitle())
                .content(mainReview.getContent())
                .rating(mainReview.getRating())
                .reviewTime(mainReview.getReviewTime())
                .build();
        if (mainReview.getReviewType() == ReviewType.PROFILE_TO_MEMBER) {
            getReviewResponseDto.setUserId(mainReview.getProfile().getProfileId());
            getReviewResponseDto.setUserName(mainReview.getProfile().getPetName());
            getReviewResponseDto.setUserImageUrl(mainReview.getProfile().getPetImageUrl());
            getReviewResponseDto.setOwner(checkOwner(member, mainReview.getProfile().getMember()));
        } else {
            getReviewResponseDto.setUserId(mainReview.getMember().getMemberId());
            getReviewResponseDto.setUserName(mainReview.getMember().getName());
            getReviewResponseDto.setUserImageUrl(mainReview.getMember().getMemberImageUrl());
            getReviewResponseDto.setOwner(checkOwner(member, mainReview.getMember()));
        }
        return ResponseEntity.ok(getReviewResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("updateReview 요청 reviewId : {}, memberId : {}", reviewId, member.getMemberId());
        Optional<Review> review = reviewRepository.findById(reviewId);

        if (review.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        }
        Review mainReview = review.get();
        if (mainReview.getReviewType() == ReviewType.MEMBER_TO_PROFILE) {
            if (!(mainReview.getMember().equals(member))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한 없음.");
            }
        } else if (mainReview.getReviewType() == ReviewType.PROFILE_TO_MEMBER) {
            if (!(mainReview.getProfile().getMember().equals(member))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한 없음.");
            }
        }
        mainReview.setTitle(updateReviewDto.getTitle());
        mainReview.setContent(updateReviewDto.getContent());
        mainReview.setRating(updateReviewDto.getRating());

        return ResponseEntity.ok().body("수정 완료");
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteReview(Long reviewId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("deleteReview 요청 reviewId : {}, memberId : {}", reviewId, member.getMemberId());
        Optional<Review> review = reviewRepository.findById(reviewId);

        if (review.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책기록은 없습니다.");
        }
        Review mainReview = review.get();
        if (mainReview.getReviewType() == ReviewType.MEMBER_TO_PROFILE) {
            if (!(mainReview.getMember().equals(member))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한 없음.");
            }
        } else if (mainReview.getReviewType() == ReviewType.PROFILE_TO_MEMBER) {
            if (!(mainReview.getProfile().getMember().equals(member))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한 없음.");
            }
        }

        reviewRepository.deleteById(mainReview.getReviewId());

        return ResponseEntity.ok().body("삭제 완료.");
    }

    private static boolean checkOwner(Member ownerMember, Member member) {
        if (ownerMember.equals(member)) {
            return true;
        }
        return false;
    }
}
