package com.example.PetApp.service.review;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.Review;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.review.*;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ReviewMapper;
import com.example.PetApp.query.MemberQueryService;
import com.example.PetApp.query.ProfileQueryService;
import com.example.PetApp.query.ReviewQueryService;
import com.example.PetApp.query.WalkRecordQueryService;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.ReviewRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.PetApp.domain.Review.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final ReviewQueryService reviewQueryService;
    private final WalkRecordQueryService walkRecordQueryService;
    private final MemberQueryService memberQueryService;
    private final ProfileQueryService profileQueryService;

    @Transactional
    @Override
    public CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, String email) {
        log.info("createReview 요청 walkRecord : {}, email : {}", createReviewDto.getWalkRecordId(), email);
        Member member = memberQueryService.findByMember(email);
        WalkRecord walkRecord = walkRecordQueryService.findByWalkRecord(createReviewDto.getWalkRecordId());
        if (walkRecord.getWalkStatus() != WalkRecord.WalkStatus.FINISH) {
            throw new ConflictException("산책을 다해야 후기를 작성할 수 있습니다.");
        } else if (!(walkRecord.getMember().equals(member))) {
            throw new ForbiddenException("권한 없음.");
        }
        Review savedReview = reviewRepository.save(ReviewMapper.toEntity(walkRecord, createReviewDto));
        return new CreateReviewResponseDto(savedReview.getReviewId());
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByMember(Long memberId, String email) {
        log.info("getReviewListByMember 요청 memberId : {}, email : {}", memberId, email);
        Member member = memberQueryService.findByMember(email);
        Member ownerMember = memberQueryService.findByMember(memberId);
        List<Review> reviewList = reviewRepository.findAllByMemberAndReviewType(member, ReviewType.PROFILE_TO_MEMBER);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, ownerMember.getMemberId(), ownerMember.getName(), ownerMember.getMemberImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));
    }

    @Transactional(readOnly = true)
    @Override
    public GetReviewListResponseDto getReviewListByProfile(Long profileId, String email) {
        log.info("getReviewListByProfile 요청 profileId : {}, email : {}", profileId, email);
        Member member = memberQueryService.findByMember(email);
        Profile profile = profileQueryService.findByProfile(profileId);
        List<Review> reviewList = reviewRepository.findAllByProfileAndReviewType(profile, ReviewType.MEMBER_TO_PROFILE);
        return ReviewMapper.toGetReviewListResponseDto(reviewList, profile.getProfileId(), profile.getPetName(), profile.getPetImageUrl(), ReviewMapper.toGetReviewList(reviewList, member));

    }


    @Transactional(readOnly = true)
    @Override
    public GetReviewResponseDto getReview(Long reviewId, String email) {
        log.info("getReview 요청 reviewId : {}, email : {}", reviewId,email);
        Member member = memberQueryService.findByMember(email);
        Review review = reviewQueryService.findByReview(reviewId);
        return ReviewMapper.toGetReviewResponseDto(review, member);
    }

    @Transactional
    @Override
    public void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email) {
        log.info("updateReview 요청 reviewId : {}, email : {}", reviewId, email);
        Review review = findReviewWithAuth(reviewId, email);

        review.setContent(new Content(updateReviewDto.getTitle(), updateReviewDto.getContent()));
        review.setRating(updateReviewDto.getRating());
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, String email) {
        log.info("deleteReview 요청 reviewId : {}, email : {}", reviewId, email);
        Review review = findReviewWithAuth(reviewId, email);

        reviewRepository.deleteById(review.getReviewId());
    }

    private Review findReviewWithAuth(Long reviewId, String email) {
        Member member = memberQueryService.findByMember(email);
        Review review = reviewQueryService.findByReview(reviewId);

        if (review.getReviewType() == ReviewType.MEMBER_TO_PROFILE) {
            if (!review.getMember().equals(member)) {
                throw new ForbiddenException("권한이 없습니다.");
            }
        } else if (review.getReviewType() == ReviewType.PROFILE_TO_MEMBER) {
            if (!review.getProfile().getMember().equals(member)) {
                throw new ForbiddenException("권한이 없습니다.");
            }
        }

        return review;
    }

}
