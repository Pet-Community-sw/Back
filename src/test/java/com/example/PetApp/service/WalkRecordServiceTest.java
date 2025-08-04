package com.example.PetApp.service;

import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.dto.walkrecord.CreateWalkRecordResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordLocationResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordResponseDto;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.WalkRecordMapper;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import com.example.PetApp.service.walkrecord.WalkRecordServiceImpl;
import com.example.PetApp.util.DistanceUtil;
import com.example.PetApp.util.SendNotificationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalkRecordServiceTest {

    @InjectMocks
    private WalkRecordServiceImpl walkRecordServiceImpl;
    @Mock
    private WalkRecordRepository walkRecordRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private SendNotificationUtil sendNotificationUtil;
    @Mock
    private ListOperations<String, String> listOperations;


    @Test
    @DisplayName("createWalkRecord_성공")
    void test1() {
        // given
        Long selectedMemberId = 1L;
        Long walkRecordId = 100L;

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .selectedApplicantMemberId(selectedMemberId)
                .build();

        Member member = Member.builder()
                .memberId(selectedMemberId)
                .email("test")
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .walkRecordId(walkRecordId)
                .member(member)
                .build();

        when(memberRepository.findById(delegateWalkPost.getSelectedApplicantMemberId())).thenReturn(Optional.of(Member.builder().memberId(1L).build()));
        try(MockedStatic<WalkRecordMapper> mockedStatic = mockStatic(WalkRecordMapper.class)) {
            mockedStatic.when(() -> WalkRecordMapper.toEntity(delegateWalkPost, member)).thenReturn(walkRecord);
            when(walkRecordRepository.save(any(WalkRecord.class))).thenReturn(WalkRecord.builder().walkRecordId(100L).build());

            //when
            CreateWalkRecordResponseDto result = walkRecordServiceImpl.createWalkRecord(delegateWalkPost);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getWalkRecordId()).isEqualTo(walkRecordId);
        }
    }

    @Test
    @DisplayName("createWalkRecord_대리산책자 유저가 없는 경우_실패")
    void test2() {
        //given
        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .selectedApplicantMemberId(1L)
                .build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.createWalkRecord(delegateWalkPost))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 대리산책자 유저가 없습니다.");
    }

    @Test
    @DisplayName("getWalkRecord_성공")
    void test3() {
        // given
        Long walkRecordId = 1L;
        String email = "test";

        WalkRecord walkRecord = WalkRecord.builder()
                .walkRecordId(walkRecordId)
                .startTime(LocalDateTime.of(2025, 6, 10, 10, 0))
                .finishTime(LocalDateTime.of(2025, 6, 10, 11, 0))
                .walkDistance(3.5)
                .pathPoints(List.of("point1", "point2"))
                .build();

        GetWalkRecordResponseDto getWalkRecordResponseDto = GetWalkRecordResponseDto.builder()
                .walkRecordId(walkRecordId)
                .startTime(walkRecord.getStartTime())
                .finishTime(walkRecord.getFinishTime())
                .walkDistance(walkRecord.getWalkDistance())
                .walkTime("1시간 0분 0초")
                .pathPoints(walkRecord.getPathPoints())
                .build();

        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));
        try (MockedStatic<WalkRecordMapper> mockedStatic = mockStatic(WalkRecordMapper.class)) {
            mockedStatic.when(() -> WalkRecordMapper.toGetWalkRecordResponseDto(walkRecord))
                    .thenReturn(getWalkRecordResponseDto);

            // when
            GetWalkRecordResponseDto result = walkRecordServiceImpl.getWalkRecord(walkRecordId, email);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getWalkRecordId()).isEqualTo(walkRecordId);
            assertThat(walkRecord.getWalkDistance()).isEqualTo(result.getWalkDistance());
            assertThat(walkRecord.getStartTime()).isEqualTo(result.getStartTime());
            assertThat(walkRecord.getFinishTime()).isEqualTo(result.getFinishTime());
            assertThat(walkRecord.getPathPoints()).isEqualTo(result.getPathPoints());
            assertThat("1시간 0분 0초").isEqualTo(result.getWalkTime());
        }
    }

    @Test
    @DisplayName("getWalkRecord_산책기록이 없는 경우_실패")
    void test4() {
        // given
        Long walkRecordId = 1L;
        String email = "test";

        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.getWalkRecord(walkRecordId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책기록은 없습니다.");
    }

    @Test
    @DisplayName("getWalkRecordLocation_성공")
    void test5() {
        //given
        String email = "test";
        Long walkRecordId = 1L;
        String location = "Location : 127.03, 36.2";

        Member member = Member.builder().build();

        Profile profile = Profile.builder()
                .member(member)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .profile(profile)
                .build();

        WalkRecord walkRecord=WalkRecord.builder()
                .delegateWalkPost(delegateWalkPost)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.index("walk:path:" + walkRecordId, -1)).thenReturn(location);

        //when
        GetWalkRecordLocationResponseDto result = walkRecordServiceImpl.getWalkRecordLocation(walkRecordId, email);

        //then
        assertThat(result.getLastLocation()).isEqualTo(location);
    }

    @Test
    @DisplayName("getWalkRecordLocation_산책기록이 없는 경우_실패")
    void test6() {
        // given
        Long walkRecordId = 1L;
        String email = "test";
        Member member = Member.builder().build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.getWalkRecordLocation(walkRecordId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책기록이 없습니다.");
    }

    @Test
    @DisplayName("getWalkRecordLocation_권한이 없는 경우_실패")
    void test7() {
        //given
        String email = "test";
        Long walkRecordId = 1L;

        Member member = Member.builder().memberId(1L).build();
        Member fakeMember = Member.builder().memberId(2L).build();

        Profile profile = Profile.builder()
                .member(fakeMember)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .profile(profile)
                .build();

        WalkRecord walkRecord=WalkRecord.builder()
                .delegateWalkPost(delegateWalkPost)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));

        //when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.getWalkRecordLocation(walkRecordId, email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한 없음.");
    }

    @Test
    @DisplayName("updateStartWalkRecord_성공")
    void test8() {
        // given
        String email = "test";
        Long walkRecordId = 1L;
        Long memberId = 2L;

        Member member = Member.builder()
                .memberId(memberId)
                .email(email)
                .name("초이선자이")
                .build();

        Member fakeMember = Member.builder()
                .memberId(200L)
                .name("작성자")
                .build();

        Profile profile = Profile.builder()
                .member(fakeMember)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .profile(profile)
                .selectedApplicantMemberId(memberId)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .walkRecordId(walkRecordId)
                .member(member)
                .delegateWalkPost(delegateWalkPost)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));

        // when
        walkRecordServiceImpl.updateStartWalkRecord(walkRecordId, email);

        // then
        assertThat(walkRecord.getWalkStatus()).isEqualTo(WalkRecord.WalkStatus.START);
        assertThat(walkRecord.getStartTime()).isNotNull();

        verify(sendNotificationUtil).sendNotification(eq(fakeMember), eq("초이선자이님이 산책을 시작하였습니다."));
    }

    @Test
    @DisplayName("updateStartWalkRecord_산책기록이 없는 경우_실패")
    void test9() {
        // given
        String email = "test";
        Long walkRecordId = 1L;
        Member member = Member.builder().build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.updateStartWalkRecord(walkRecordId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책기록은 없습니다.");
    }

    @Test
    @DisplayName("updateStartWalkRecord_권한이 없는 경우_실패")
    void test10() {
        // given
        String email = "test@";
        Long walkRecordId = 1L;
        Long memberId = 1L;
        Long fakeMemberId = 2L;

        Member member = Member.builder()
                .memberId(memberId)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .selectedApplicantMemberId(fakeMemberId)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .delegateWalkPost(delegateWalkPost)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));

        // when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.updateStartWalkRecord(walkRecordId, email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한 없음.");
    }

    @Test
    @DisplayName("updateFinishWalkRecord_성공")
    void test11() {
        // given
        Long walkRecordId = 1L;
        String email = "test";
        Long memberId = 2L;

        Member member = Member.builder()
                .memberId(memberId)
                .name("초이선자이")
                .build();

        Member fakeMember = Member.builder()
                .memberId(20L)
                .build();

        Profile profile = Profile.builder()
                .member(fakeMember)
                .build();

        DelegateWalkPost delegateWalkPost = DelegateWalkPost.builder()
                .profile(profile)
                .selectedApplicantMemberId(memberId)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .walkRecordId(walkRecordId)
                .member(member)
                .delegateWalkPost(delegateWalkPost)
                .build();

        List<String> paths = List.of("127.0,36.2", "127.01,36.201");

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);//opsForList가 null이기 때문에 ListOperations<String, String>선언
        when(listOperations.range("walk:path:" + walkRecordId, 0, -1)).thenReturn(paths);
        try(MockedStatic<DistanceUtil> mockedStatic = mockStatic(DistanceUtil.class)) {
            mockedStatic.when(() -> DistanceUtil.calculateTotalDistance(paths)).thenReturn(1.2);

            // when
            walkRecordServiceImpl.updateFinishWalkRecord(walkRecordId, email);

            // then
            assertThat(walkRecord.getWalkStatus()).isEqualTo(WalkRecord.WalkStatus.FINISH);
            assertThat(walkRecord.getFinishTime()).isNotNull();
            assertThat(walkRecord.getWalkDistance()).isEqualTo(1.2);
            assertThat(walkRecord.getPathPoints()).isEqualTo(paths);

            verify(stringRedisTemplate).delete("walk:path:" + walkRecordId);
            verify(sendNotificationUtil).sendNotification(eq(fakeMember), eq("초이선자이님이 산책을 마쳤습니다. 후기를 작성해주세요."));
        }
    }


    @Test
    @DisplayName("updateFinishWalkRecord_산책기록이 없는 경우_실패")
    void test12() {
        // given
        String email = "test";
        Long walkRecordId = 1L;

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(Member.builder().build()));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.updateFinishWalkRecord(walkRecordId, email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책기록은 없습니다.");
    }

    @Test
    @DisplayName("updateFinishWalkRecord_권한이 없는 경우_실패")
    void test13() {
        // given
        String email = "test";
        Long walkRecordId = 1L;

        Member requester = Member.builder().memberId(1L).build();

        DelegateWalkPost post = DelegateWalkPost.builder()
                .selectedApplicantMemberId(2L)
                .build();

        WalkRecord walkRecord = WalkRecord.builder()
                .delegateWalkPost(post)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(requester));
        when(walkRecordRepository.findById(walkRecordId)).thenReturn(Optional.of(walkRecord));

        // when & then
        assertThatThrownBy(() -> walkRecordServiceImpl.updateFinishWalkRecord(walkRecordId, email))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한 없음.");
    }






}

