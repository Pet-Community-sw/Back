package com.example.PetApp.service;

import com.example.PetApp.domain.Applicant;
import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.delegateWalkpost.*;
import com.example.PetApp.dto.memberchat.CreateMemberChatRoomResponseDto;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.DelegateWalkPostMapper;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.service.memberchatRoom.MemberChatRoomService;
import com.example.PetApp.service.walkerpost.DelegateWalkPostServiceImp;
import com.example.PetApp.service.walkrecord.WalkRecordService;
import com.example.PetApp.util.SendNotificationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DelegateWalkPostServiceTest {

    @InjectMocks
    private DelegateWalkPostServiceImp delegateWalkPostServiceImp;
    @Mock
    private DelegateWalkPostRepository delegateWalkPostRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberChatRoomService memberChatRoomService;
    @Mock
    private WalkRecordService walkRecordService;
    @Mock
    private SendNotificationUtil sendNotificationUtil;

    @Test
    @DisplayName("createDelegateWalkPost_성공")
    void test1() {
        //given
        Long profileId = 1L;
        Profile profile = Profile.builder()
                .profileId(1L)
                .build();

        CreateDelegateWalkPostDto createDelegateWalkPostDto = CreateDelegateWalkPostDto.builder()
                .title("a")
                .content("b")
                .price(1000L)
                .locationLongitude(127.0)
                .locationLatitude(32.0)
                .allowedRadiusMeters(1000)
                .scheduledTime(LocalDateTime.now())
                .requireProfile(true)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(delegateWalkPostRepository.save(any(DelegateWalkPost.class))).thenAnswer(invocation -> {
            DelegateWalkPost delegateWalkPost = invocation.getArgument(0);
            return delegateWalkPost.toBuilder().delegateWalkPostId(1L).build();
        });

        //when
        CreateDelegateWalkPostResponseDto result = delegateWalkPostServiceImp.createDelegateWalkPost(createDelegateWalkPostDto, profileId);

        //then
        assertThat(result).isNotNull();
//        assertThat(result.getDelegateWalkPostId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createDelegateWalkPost_프로필 없는 경우_실패")
    void test2() {
        //given
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.createDelegateWalkPost(any(CreateDelegateWalkPostDto.class), 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 등록해주세요.");
    }

    @Test
    @DisplayName("getDelegateWalkPostsByLocation_성공")
    void test3() {
        // given
        String email = "test";
        Double minLon = 127.0, minLat = 37.0, maxLon = 128.0, maxLat = 38.0;

        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .name("테스트")
                .build();

        Profile profile = Profile.builder()
                .profileId(1L)
                .petName("a")
                .petImageUrl("as")
                .build();

        DelegateWalkPost post1 = DelegateWalkPost.builder()
                .delegateWalkPostId(1L)
                .title("산책 대행 1")
                .profile(profile)
                .delegateWalkPostTime(LocalDateTime.now())
                .build();

        DelegateWalkPost post2 = DelegateWalkPost.builder()
                .delegateWalkPostId(2L)
                .title("산책 대행 2")
                .profile(profile)
                .delegateWalkPostTime(LocalDateTime.now())
                .build();

        List<DelegateWalkPost> postList = List.of(post1, post2);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findByDelegateWalkPostByLocation(
                minLon - 0.01, minLat - 0.01, maxLon + 0.01, maxLat + 0.01))
                .thenReturn(postList);

        // when
        List<GetDelegateWalkPostsResponseDto> result = delegateWalkPostServiceImp
                .getDelegateWalkPostsByLocation(minLon, minLat, maxLon, maxLat, email);

        // then
        assertThat(result).hasSize(2);
        verify(memberRepository).findByEmail(email);
        verify(delegateWalkPostRepository).findByDelegateWalkPostByLocation(
                126.99, 36.99, 128.01, 38.01);
    }

    @Test
    @DisplayName("getDelegateWalkPostsByPlace_성공")
    void test4() {
        // given
        String email = "test";
        double longitude = 127.03;
        double latitude = 37.56;

        Profile profile = Profile.builder()
                .profileId(1L)
                .petName("a")
                .petImageUrl("as")
                .build();

        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .name("테스터")
                .build();

        DelegateWalkPost post1 = DelegateWalkPost.builder()
                .delegateWalkPostId(1L)
                .title("도심 산책 대행")
                .profile(profile)
                .delegateWalkPostTime(LocalDateTime.now())
                .build();

        DelegateWalkPost post2 = DelegateWalkPost.builder()
                .delegateWalkPostId(2L)
                .title("강변 산책 대행")
                .profile(profile)
                .delegateWalkPostTime(LocalDateTime.now())
                .build();

        List<DelegateWalkPost> postList = List.of(post1, post2);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findByDelegateWalkPostByPlace(longitude, latitude)).thenReturn(postList);

        // when
        List<GetDelegateWalkPostsResponseDto> result = delegateWalkPostServiceImp.getDelegateWalkPostsByPlace(longitude, latitude, email);

        // then
        assertThat(result).hasSize(2);
        verify(memberRepository).findByEmail(email);
        verify(delegateWalkPostRepository).findByDelegateWalkPostByPlace(longitude, latitude);
    }

    @Test
    @DisplayName("getDelegateWalkPost_성공")
    void test5() {
        // given
        Long postId = 1L;
        String email = "test";

        Profile profile = Profile.builder()
                .profileId(1L)
                .petName("a")
                .petImageUrl("b")
                .build();

        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .build();

        DelegateWalkPost post = DelegateWalkPost.builder()
                .delegateWalkPostId(postId)
                .title("산책 요청")
                .profile(profile)
                .delegateWalkPostTime(LocalDateTime.now())
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        GetPostResponseDto result = delegateWalkPostServiceImp.getDelegateWalkPost(postId, email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPetName()).isEqualTo("a");
    }

    @Test
    @DisplayName("getDelegateWalkPost_대리산책자 게시글이 없는 경우_실패")
    void test6() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        Member member = Member.builder()
                .email(email)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.getDelegateWalkPost(delegateWalkPostId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대리산책자 게시글은 없습니다.");
    }

    @Test
    @DisplayName("getDelegateWalkPost_프로필을 등록한 사람만 볼 수 있는 경우_실패")
    void test7() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        Member member = Member.builder()
                .email(email)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(delegateWalkPostId)
                .requireProfile(true)
                .build();


        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.getDelegateWalkPost(delegateWalkPostId, email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 등록해주세요.");
    }

    @Test
    @DisplayName("getApplicants_성공")
    void test8() {
        //given
        Long profileId = 1L;
        Long delegateWalkPostId = 2L;

        Profile profile = Profile.builder()
                .profileId(1L)
                .build();

        Applicant applicant = Applicant.builder()
                .memberId(1L)
                .content("잘 할 수있어용")
                .build();

        Applicant applicant2 = Applicant.builder()
                .memberId(2L)
                .content("잘 할 수 있습니데이")
                .build();

        Set<Applicant> applicants = new HashSet<>();
        applicants.add(applicant2);
        applicants.add(applicant);

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .applicants(applicants)
                .profile(profile)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        //when
        Set<Applicant> result = delegateWalkPostServiceImp.getApplicants(delegateWalkPostId, profileId);

        //then
        assertThat(result).containsExactlyInAnyOrder(applicant, applicant2);
    }

    @Test
    @DisplayName("getApplicants_프로필이 없는 경우_실패")
    void test9() {
        //given
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.getApplicants(anyLong(), 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("프로필 등록해주세요.");
    }

    @Test
    @DisplayName("getApplicants_대리산책자 게시글이 없는 경우_실패")
    void test10() {
        //given
        Long profileId = 1L;
        Profile profile = Profile.builder()
                .profileId(1L)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(delegateWalkPostRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.getApplicants(1L, profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대리산책자 게시글은 없습니다.");
    }

    @Test
    @DisplayName("getApplicants_글 작성자가 아닌 다른 사용자가 요청한 경우_실패")
    void test11() {
        //given
        Long profileId = 1L;
        Long delegateWalkPostId = 2L;

        Profile profile = Profile.builder()
                .profileId(1L)
                .build();

        Profile fakeProfile = Profile.builder()
                .profileId(2L)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(2L)
                .profile(fakeProfile)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.getApplicants(delegateWalkPostId, profileId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한 없음.");
    }

    @Test
    @DisplayName("applyToDelegateWalkPost_성공")
    void test12() {
        // given
        Long delegateWalkPostId = 1L;
        String content = "산책 자신 있어요!";
        String email = "test";

        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .build();

        Profile fakeProfile = Profile.builder()
                .profileId(2L)
                .member(Member.builder().memberId(3L).name("게시글주인").build())
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(delegateWalkPostId)
                .status(DelegateWalkPost.DelegateWalkStatus.RECRUITING)
                .applicants(new HashSet<>())
                .profile(fakeProfile)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        try (MockedStatic<DelegateWalkPostMapper> mockStatic = mockStatic(DelegateWalkPostMapper.class)) {
            mockStatic.when(() -> DelegateWalkPostMapper.filter(delegateWalkPost, member)).thenReturn(false);

            // when
            ApplyToDelegateWalkPostResponseDto result = delegateWalkPostServiceImp.applyToDelegateWalkPost(delegateWalkPostId, content, email);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getMemberId()).isEqualTo(1L);
            assertThat(delegateWalkPost.getApplicants()).hasSize(1);
        }
    }

    @Test
    @DisplayName("applyToDelegateWalkPost_대리산책자 게시글이 없는 경우_실패")
    void test13() {
        //given
        String email = "test";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.ofNullable(Member.builder().memberId(1L).build()));
        when(delegateWalkPostRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.applyToDelegateWalkPost(1L, anyString(), email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대리산책자 게시글은 없습니다.");
    }

    @Test
    @DisplayName("applyToDelegateWalkPost_프로필 등록이 필수인경우_실패")
    void test14() {
        // given
        String email = "test";
        Long postId = 1L;
        Member member = Member.builder().memberId(10L).email(email).build();
        DelegateWalkPost post = DelegateWalkPost.builder()
                .delegateWalkPostId(postId)
                .status(DelegateWalkPost.DelegateWalkStatus.RECRUITING)
                .applicants(new HashSet<>())
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(postId)).thenReturn(Optional.of(post));

        try (MockedStatic<DelegateWalkPostMapper> mockStatic = mockStatic(DelegateWalkPostMapper.class)) {
            mockStatic.when(() -> DelegateWalkPostMapper.filter(post, member)).thenReturn(true);
            // when & then
            assertThatThrownBy(() -> delegateWalkPostServiceImp.applyToDelegateWalkPost(postId, "지원합니다!", email))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("프로필 등록해주세요.");
        }
    }

    @Test
    @DisplayName("applyToDelegateWalkPost_이미 지원한 회원인 경우_실패")
    void test15() {
        // given
        String email = "test";
        Long postId = 1L;
        Long memberId = 2L;
        Member member = Member.builder().memberId(memberId).email(email).build();
        Applicant applicant = Applicant.builder().memberId(memberId).content("이전에 지원").build();
        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(postId)
                .status(DelegateWalkPost.DelegateWalkStatus.RECRUITING)
                .applicants(new HashSet<>(Set.of(applicant)))
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(postId)).thenReturn(Optional.of(delegateWalkPost));

        try (MockedStatic<DelegateWalkPostMapper> mockStatic = mockStatic(DelegateWalkPostMapper.class)) {
            mockStatic.when(() -> DelegateWalkPostMapper.filter(delegateWalkPost, member)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> delegateWalkPostServiceImp.applyToDelegateWalkPost(postId, "또 지원", email))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("이미 신청한 회원입니다.");

        }
    }

    @Test
    @DisplayName("applyToDelegateWalkPost_모집 완료인 경우_실패")
    void test16() {
        // given
        String email = "test";
        Long postId = 1L;
        Member member = Member.builder().memberId(10L).email(email).build();
        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(postId)
                .status(DelegateWalkPost.DelegateWalkStatus.COMPLETED)
                .applicants(new HashSet<>())
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(postId)).thenReturn(Optional.of(delegateWalkPost));

        try (MockedStatic<DelegateWalkPostMapper> mockStatic = mockStatic(DelegateWalkPostMapper.class)) {
            mockStatic.when(() -> DelegateWalkPostMapper.filter(delegateWalkPost, member)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> delegateWalkPostServiceImp.applyToDelegateWalkPost(postId, "지각 지원", email))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("모집 완료 게시글입니다.");
        }
    }

    @Test
    @DisplayName("selectApplicant_성공")
    void test17() {
        // given
        Long delegateWalkPostId = 1L;
        Long applicantMemberId = 2L;
        String email = "test";

        Member member = Member.builder()
                .memberId(1L)
                .build();

        Member fakeMember = Member.builder()
                .memberId(applicantMemberId)
                .build();

        Profile profile = Profile.builder()
                .profileId(3L)
                .member(member)
                .build();

        Applicant applicant = Applicant.builder()
                .memberId(applicantMemberId)
                .content("지원합니다!")
                .build();

        DelegateWalkPost post = DelegateWalkPost.builder()
                .delegateWalkPostId(delegateWalkPostId)
                .profile(profile)
                .status(DelegateWalkPost.DelegateWalkStatus.RECRUITING)
                .applicants(new HashSet<>(Set.of(applicant)))
                .build();

        CreateMemberChatRoomResponseDto chatRoomDto = new CreateMemberChatRoomResponseDto(1L);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findById(applicantMemberId)).thenReturn(Optional.of(fakeMember));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(post));

        when(memberChatRoomService.createMemberChatRoom(member, fakeMember)).thenReturn(chatRoomDto);

        // when
        CreateMemberChatRoomResponseDto result = delegateWalkPostServiceImp.selectApplicant(delegateWalkPostId, applicantMemberId, email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMemberChatRoomId()).isEqualTo(1L);
        assertThat(post.getStatus()).isEqualTo(DelegateWalkPost.DelegateWalkStatus.COMPLETED);
        assertThat(post.getSelectedApplicantMemberId()).isEqualTo(applicantMemberId);

    }

    @Test
    @DisplayName("selectApplicant_지원자가 없는 경우_실패")
    void test18() {
        //given
        Long applicantMemberId = 2L;
        when(memberRepository.findByEmail("test")).thenReturn(Optional.ofNullable(Member.builder().build()));
        when(memberRepository.findById(applicantMemberId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.selectApplicant(anyLong(), applicantMemberId, "test"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 지원자는 없습니다.");
    }


    @Test
    @DisplayName("selectApplicant_대리 산책자 게시글이 없는 경우_실패")
    void test19() {
        Long delegateWalkPostId = 1L;
        Long applicantMemberId = 2L;
        String email = "test";
        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findById(applicantMemberId)).thenReturn(Optional.of(Member.builder().build()));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> delegateWalkPostServiceImp.selectApplicant(delegateWalkPostId, applicantMemberId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대리산책자 게시글은 없습니다.");
    }

    @Test
    @DisplayName("selectApplicant_권한이 없는 경우_실패")
    void test20() {
        Long delegateWalkPostId = 1L;
        Long applicantMemberId = 2L;
        String email = "test";

        Member fakeMember = Member.builder()
                .memberId(1L)
                .email("test")
                .build();

        Member member = Member.builder()
                .memberId(2L)
                .email(email)
                .build();

        Member applicantMember = Member.builder()
                .memberId(applicantMemberId)
                .build();

        Profile profile = Profile.builder()
                .profileId(3L)
                .member(fakeMember)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(delegateWalkPostId)
                .profile(profile)
                .status(DelegateWalkPost.DelegateWalkStatus.RECRUITING)
                .applicants(new HashSet<>(Set.of(
                        Applicant.builder().memberId(applicantMemberId).build()
                )))
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findById(applicantMemberId)).thenReturn(Optional.of(applicantMember));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        assertThatThrownBy(() -> delegateWalkPostServiceImp.selectApplicant(delegateWalkPostId, applicantMemberId, email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한 없음.");

    }

    @Test
    @DisplayName("selectApplicant_지원자 목록에 해당 지원자가 없는 경우_실패")
    void test21() {
        Long delegateWalkPostId = 1L;
        Long applicantMemberId = 2L;
        String email = "test";

        Member member = Member.builder()
                .memberId(3L)
                .email(email)
                .build();

        Member applicantMember = Member.builder()
                .memberId(applicantMemberId)
                .email("test").build();

        Profile postProfile = Profile.builder()
                .profileId(4L)
                .member(member)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(delegateWalkPostId)
                .profile(postProfile)
                .status(DelegateWalkPost.DelegateWalkStatus.RECRUITING)
                .applicants(new HashSet<>())
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findById(applicantMemberId)).thenReturn(Optional.of(applicantMember));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        assertThatThrownBy(() -> delegateWalkPostServiceImp.selectApplicant(delegateWalkPostId, applicantMemberId, email))
                .isInstanceOf(ConflictException.class)
                .hasMessage("해당 지원자는 없습니다.");
    }

    @Test
    @DisplayName("updateDelegateWalkPost_성공")
    void test22() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        Member member = Member.builder()
                .memberId(1L)
                .build();

        Profile profile = Profile.builder()
                .profileId(3L)
                .member(member)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(2L)
                .profile(profile)
                .content("a")
                .build();

        UpdateDelegateWalkPostDto updateDelegateWalkPostDto = UpdateDelegateWalkPostDto.builder()
                .title("aa")
                .content("bb")
                .price(2000L)
                .allowedRedisMeters(10000)
                .requireProfile(true)
                .scheduledTime(LocalDateTime.now())
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        //when
        delegateWalkPostServiceImp.updateDelegateWalkPost(delegateWalkPostId, updateDelegateWalkPostDto, email);

        //then
        assertThat(delegateWalkPost.getContent()).isEqualTo("bb");

    }

    @Test
    @DisplayName("updateDelegateWalkPost_대리산책자 게시글이 없는 경우_실패")
    void test23() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(Member.builder().build()));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.updateDelegateWalkPost(delegateWalkPostId, any(UpdateDelegateWalkPostDto.class), email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대리산책자 게시글은 없습니다.");
    }

    @Test
    @DisplayName("updateDelegateWalkPost_수정 권한이 없는 경우_실패")
    void test24() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        Member member = Member.builder()
                .memberId(1L)
                .build();

        Member fakeMember = Member.builder()
                .memberId(2L)
                .build();

        Profile profile = Profile.builder()
                .member(fakeMember)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .profile(profile)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.updateDelegateWalkPost(delegateWalkPostId, any(UpdateDelegateWalkPostDto.class), email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("수정 권한 없음.");
    }

    @Test
    @DisplayName("deleteDelegateWalkPost_성공")
    void test25() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        Member member = Member.builder()
                .memberId(1L)
                .build();

        Profile profile = Profile.builder()
                .profileId(3L)
                .member(member)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .delegateWalkPostId(2L)
                .profile(profile)
                .content("a")
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        //when
        delegateWalkPostServiceImp.deleteDelegateWalkPost(delegateWalkPostId, email);

        //then
        verify(delegateWalkPostRepository).deleteById(delegateWalkPostId);

    }

    @Test
    @DisplayName("deleteDelegateWalkPost_대리산책자 게시글이 없는 경우_실패")
    void test26() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(Member.builder().build()));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.deleteDelegateWalkPost(delegateWalkPostId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대리산책자 게시글은 없습니다.");
    }

    @Test
    @DisplayName("deleteDelegateWalkPost_삭제 권한이 없는 경우_실패")
    void test27() {
        //given
        String email = "test";
        Long delegateWalkPostId = 1L;

        Member member = Member.builder()
                .memberId(1L)
                .build();

        Member fakeMember = Member.builder()
                .memberId(2L)
                .build();

        Profile profile = Profile.builder()
                .member(fakeMember)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .profile(profile)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findById(delegateWalkPostId)).thenReturn(Optional.of(delegateWalkPost));

        //when & then
        assertThatThrownBy(() -> delegateWalkPostServiceImp.deleteDelegateWalkPost(delegateWalkPostId, email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("삭제 권한 없음.");
    }


}




