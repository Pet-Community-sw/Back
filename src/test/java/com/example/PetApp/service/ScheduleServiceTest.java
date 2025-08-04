package com.example.PetApp.service;


import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.dto.schedule.GetSchedulesResponseDto;
import com.example.PetApp.dto.schedule.ScheduleType;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.WalkingTogetherPostRepository;
import com.example.PetApp.service.schedule.ScheduleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @InjectMocks
    private ScheduleServiceImpl scheduleServiceImpl;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private WalkingTogetherPostRepository walkingTogetherPostRepository;
    @Mock
    private DelegateWalkPostRepository delegateWalkPostRepository;

    @Test
    @DisplayName("getSchedules_성공")
    void test1() {
        // Given
        Long profileId = 1L;
        String start = "2025-06-01";
        String end = "2025-06-30";

        LocalDateTime startDateTime = LocalDateTime.of(2025, 6, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 6, 30, 23, 59);

        Member member = Member.builder().memberId(10L).email("test").build();
        Profile profile = Profile.builder().profileId(profileId).member(member).build();

        WalkingTogetherPost walkPost = WalkingTogetherPost.builder()
                .scheduledTime(LocalDateTime.of(2025, 6, 10, 18, 0))
                .build();

        DelegateWalkPost delegatePost = DelegateWalkPost.builder()
                .scheduledTime(LocalDateTime.of(2025, 6, 12, 10, 0))
                .build();

        // Mocking
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(walkingTogetherPostRepository.findAllByProfileContainsAndScheduledTimeBetween(profile, startDateTime, endDateTime))
                .thenReturn(List.of(walkPost));
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(delegateWalkPostRepository.findAllBySelectedApplicantMemberIdAndScheduledTimeBetween(member.getMemberId(), startDateTime, endDateTime))
                .thenReturn(List.of(delegatePost));

        // When
        List<GetSchedulesResponseDto> result = scheduleServiceImpl.getSchedules(start, end, profileId);

        // Then
        assertThat(result).hasSize(2);

        assertThat(result).anySatisfy(dto -> {
            assertThat(dto.getScheduleType()).isEqualTo(ScheduleType.WALKING_TOGETHER);
            assertThat(dto.getScheduleDate()).isEqualTo(walkPost.getScheduledTime());
        });

        assertThat(result).anySatisfy(dto -> {
            assertThat(dto.getScheduleType()).isEqualTo(ScheduleType.DELEGATE_WALK);
            assertThat(dto.getScheduleDate()).isEqualTo(delegatePost.getScheduledTime());
        });

        // Verify
        verify(profileRepository).findById(profileId);
        verify(walkingTogetherPostRepository).findAllByProfileContainsAndScheduledTimeBetween(profile, startDateTime, endDateTime);
        verify(memberRepository).findById(member.getMemberId());
        verify(delegateWalkPostRepository).findAllBySelectedApplicantMemberIdAndScheduledTimeBetween(member.getMemberId(), startDateTime, endDateTime);
    }

}
