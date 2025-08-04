package com.example.PetApp.mapper;

import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.dto.schedule.GetSchedulesResponseDto;
import com.example.PetApp.dto.schedule.ScheduleType;

public class ScheduleMapper {

    public static GetSchedulesResponseDto toGetSchedulesResponseDto(Profile profile, WalkingTogetherPost walkingTogetherPost) {
        return GetSchedulesResponseDto.builder()
                .memberId(profile.getMember().getMemberId())
                .scheduleDate(walkingTogetherPost.getScheduledTime())
                .scheduleType(ScheduleType.WALKING_TOGETHER)
                .build();
    }
    public static GetSchedulesResponseDto toGetSchedulesResponseDto(Member member, DelegateWalkPost delegateWalkPost) {
        return GetSchedulesResponseDto.builder()
                .memberId(member.getMemberId())
                .scheduleDate(delegateWalkPost.getScheduledTime())
                .scheduleType(ScheduleType.DELEGATE_WALK)
                .build();
    }}
