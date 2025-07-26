package com.example.PetApp.service;

import com.example.PetApp.domain.*;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.review.*;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.ReviewMapper;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.ReviewRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import com.example.PetApp.service.review.ReviewServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private WalkRecordRepository walkRecordRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("createReview_성공")
    void test1() {
        //given
        String email = "test";

        CreateReviewDto createReviewDto=CreateReviewDto.builder()
                .walkRecordId(1L)
                .build();

        Member member = Member.builder()
                .memberId(1L)
                .build();

        Profile profile = Profile.builder()
                .profileId(2L)
                .member(member)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .profile(profile)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .member(member)
                .walkStatus(WalkRecord.WalkStatus.FINISH)
                .delegateWalkPost(delegateWalkPost)
                .build();

        when(walkRecordRepository.findById(createReviewDto.getWalkRecordId())).thenReturn(Optional.of(walkRecord));
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        try(MockedStatic<ReviewMapper> mockedStatic = mockStatic(ReviewMapper.class)) {
            mockedStatic.when(() -> ReviewMapper.toEntity(walkRecord, createReviewDto)).thenReturn(Review.builder().reviewId(1L).build());
            when(reviewRepository.save(any(Review.class))).thenReturn(Review.builder().reviewId(1L).build());

            //when
            CreateReviewResponseDto result = reviewServiceImpl.createReview(createReviewDto, email);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getReviewId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("createReview_산책기록이 없는 경우_실패")
    void test2() {
        //gvien
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .walkRecordId(1L)
                .build();

        when(walkRecordRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.createReview(createReviewDto, anyString()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책기록은 없습니다.");
    }

    @Test
    @DisplayName("createReview_산책을 안끝내고 후기를 작성하는 경우_실패")
    void test4() {
        //given
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .walkRecordId(1L)
                .build();


        when(walkRecordRepository.findById(anyLong())).thenReturn(Optional.of(WalkRecord.builder().walkStatus(WalkRecord.WalkStatus.START).build()));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(Member.builder().build()));

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.createReview(createReviewDto, anyString()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("산책을 다해야 후기를 작성할 수 있습니다.");
    }


    @Test
    @DisplayName("createReview_권한이 없는 경우_실패")
    void test5() {
        //given
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .walkRecordId(1L)
                .build();

        Member member = Member.builder()
                .memberId(1L)
                .build();

        Member fakeMember = Member.builder()
                .memberId(2L)
                .build();

        when(walkRecordRepository.findById(anyLong())).thenReturn(Optional.of(WalkRecord.builder().walkStatus(WalkRecord.WalkStatus.FINISH).member(member).build()));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(fakeMember));

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.createReview(createReviewDto, anyString()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한 없음.");

    }

    @Test
    @DisplayName("getReviewListByMember_성공")
    void test6() {
        // given
        String email = "test";

        Member loginMember = Member.builder()
                .memberId(2L)
                .email(email)
                .build();

        Member targetMember = Member.builder()
                .memberId(1L)
                .name("리뷰 대상자")
                .memberImageUrl("target-image.png")
                .build();

        Review review = Review.builder()
                .reviewId(10L)
                .content(new Content("산책기록 1", "좋아요"))
                .rating(5)
                .member(targetMember)
                .profile(Profile.builder()
                        .profileId(100L)
                        .petName("루비")
                        .petImageUrl("ruby.jpg")
                        .build())
                .build();

        List<Review> reviewList = List.of(review);

        List<GetReviewList> mappedList = List.of(GetReviewList.builder()
                .reviewId(10L)
                .userId(100L)
                .userName("루비")
                .userImageUrl("ruby.jpg")
                .title("좋은 후기")
                .rating(5)
                .isOwner(false)
                .build());

        GetReviewListResponseDto expected = GetReviewListResponseDto.builder()
                .userId(targetMember.getMemberId())
                .userName(targetMember.getName())
                .userImageUrl(targetMember.getMemberImageUrl())
                .averageRating(5.0)
                .reviewCount(1)
                .reviewList(mappedList)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(loginMember));
        when(memberRepository.findById(targetMember.getMemberId())).thenReturn(Optional.of(targetMember));
        when(reviewRepository.findAllByMemberAndReviewType(eq(loginMember), eq(Review.ReviewType.PROFILE_TO_MEMBER)))
                .thenReturn(reviewList);


        try (MockedStatic<ReviewMapper> mockStatic = mockStatic(ReviewMapper.class)) {
            mockStatic.when(() -> ReviewMapper.toGetReviewList(reviewList, loginMember)).thenReturn(mappedList);
            mockStatic.when(() ->
                    ReviewMapper.toGetReviewListResponseDto(
                            reviewList,
                            targetMember.getMemberId(),
                            targetMember.getName(),
                            targetMember.getMemberImageUrl(),
                            mappedList
                    )).thenReturn(expected);

            // when
            GetReviewListResponseDto result = reviewServiceImpl.getReviewListByMember(targetMember.getMemberId(), email);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.getUserName()).isEqualTo("리뷰 대상자");
            assertThat(result.getAverageRating()).isEqualTo(5.0);
        }
    }

    @Test
    @DisplayName("getReviewListByMember_해당 유저가 없는경우_실패")
    void test7() {
        //given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(Member.builder().build()));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.getReviewListByMember(1L, "test"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 유저는 없습니다.");
    }

    @Test
    @DisplayName("getReview_성공")
    void test8() {
        // given
        String email = "test";
        Long reviewId = 1L;

        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .build();

        Review review = Review.builder()
                .reviewId(reviewId)
                .content(new Content("리뷰제목", "좋아요"))
                .rating(4)
                .build();

        GetReviewResponseDto expected = GetReviewResponseDto.builder()
                .reviewId(reviewId)
                .title("리뷰제목")
                .rating(4)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        try (MockedStatic<ReviewMapper> mockStatic = mockStatic(ReviewMapper.class)) {
            mockStatic.when(() -> ReviewMapper.toGetReviewResponseDto(review, member)).thenReturn(expected);

            // when
            GetReviewResponseDto result = reviewServiceImpl.getReview(reviewId, email);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.getReviewId()).isEqualTo(reviewId);
            assertThat(result.getRating()).isEqualTo(4);
        }
    }

    @Test
    @DisplayName("getReview_산책기록이 없는 경우_실패")
    void test9() {
        // given
        String email = "test";
        Long reviewId = 1L;
        Member member = Member.builder().memberId(1L).email(email).build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewServiceImpl.getReview(reviewId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책기록은 없습니다.");
    }

    @Test
    @DisplayName("updateReview_성공")
    void test10() {
        // given
        String email = "test";
        Long reviewId = 1L;

        Member member = Member.builder()
                .memberId(10L)
                .email(email)
                .build();

        Review review = Review.builder()
                .reviewId(reviewId)
                .member(member)
                .reviewType(Review.ReviewType.MEMBER_TO_PROFILE)
                .content(new Content("aa", "bb"))
                .rating(3)
                .build();

        UpdateReviewDto updateReviewDto = UpdateReviewDto.builder()
                .title("aa")
                .content("bb")
                .rating(5)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        reviewServiceImpl.updateReview(reviewId, updateReviewDto, email);

        // then
        assertThat(review.getContent().getTitle()).isEqualTo("aa");
        assertThat(review.getContent().getContent()).isEqualTo("bb");
        assertThat(review.getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("updateReview_리뷰가 없는 경우_실패")
    void test11() {
        //given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(Member.builder().build()));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.updateReview(1L, any(UpdateReviewDto.class), "test"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 리뷰가 없습니다.");
    }

    @Test
    @DisplayName("updateReview_권한이 없는 경우_실패")
    void test12() {
        //given
        Member member = Member.builder().memberId(1L).build();

        Member fakeMember = Member.builder().memberId(2L).build();

        Review review = Review.builder()
                .member(fakeMember)
                .reviewType(Review.ReviewType.MEMBER_TO_PROFILE)
                .build();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.updateReview(1L, any(UpdateReviewDto.class), "test"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("deleteReview_성공")
    void test13() {
        // given
        String email = "test";
        Long reviewId = 1L;

        Member member = Member.builder().memberId(1L).email(email).build();

        Review review = Review.builder()
                .reviewId(reviewId)
                .reviewType(Review.ReviewType.MEMBER_TO_PROFILE)
                .member(member)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        reviewServiceImpl.deleteReview(reviewId, email);

        // then
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    @DisplayName("deleteReview_리뷰 없는 경우_실패")
    void test14() {
        //given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(Member.builder().build()));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.deleteReview(1L, "test"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 리뷰가 없습니다.");
    }

    @Test
    @DisplayName("deleteReview_권한이 없는 경우_실패")
    void test16() {
        //given
        Member member = Member.builder().memberId(1L).build();

        Member fakeMember = Member.builder().memberId(2L).build();

        Review review = Review.builder()
                .member(fakeMember)
                .reviewType(Review.ReviewType.MEMBER_TO_PROFILE)
                .build();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        //when & then
        assertThatThrownBy(() -> reviewServiceImpl.deleteReview(1L, "test"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한이 없습니다.");
    }

}



