package com.example.PetApp.service.schedule;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.WalkingTogetherMatch;
import com.example.PetApp.dto.schedule.GetSchedulesResponseDto;
import com.example.PetApp.dto.schedule.TimeDto;
import com.example.PetApp.mapper.ScheduleMapper;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.repository.jpa.WalkingTogetherPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ProfileRepository profileRepository;

    private final MemberRepository memberRepository;

    private final WalkingTogetherPostRepository walkingTogetherPostRepository;

    private final DelegateWalkPostRepository delegateWalkPostRepository;

    @Transactional(readOnly = true)
    @Override
    public List<GetSchedulesResponseDto> getSchedules(String start, String end, Long profileId) {

        List<GetSchedulesResponseDto> getSchedulesResponseDtos = new ArrayList<>();
        TimeDto timeDto = getTime(start, end);
        Optional<Profile> profile = profileRepository.findById(profileId);

        if (profile.isPresent()) {
            List<WalkingTogetherMatch> walkingTogetherMatchList =
                    walkingTogetherPostRepository.findAllByProfileContainsAndScheduledTimeBetween(profile.get(), timeDto.getStart(), timeDto.getEnd());
            List<GetSchedulesResponseDto> list = walkingTogetherMatchList.stream()
                    .map(walkingTogetherPost -> ScheduleMapper.toGetSchedulesResponseDto(profile.get(), walkingTogetherPost)
            ).collect(Collectors.toList());
            getSchedulesResponseDtos.addAll(list);

        }

        Member member = memberRepository.findById(profile.get().getMember().getMemberId()).get();
        List<DelegateWalkPost> delegateWalkPostList =
                delegateWalkPostRepository.findAllBySelectedApplicantMemberIdAndScheduledTimeBetween(member.getMemberId(), timeDto.getStart(), timeDto.getEnd());

        List<GetSchedulesResponseDto> list = delegateWalkPostList.stream()
                .map(delegateWalkPost -> ScheduleMapper.toGetSchedulesResponseDto(member, delegateWalkPost)
                ).collect(Collectors.toList());
        getSchedulesResponseDtos.addAll(list);

        return getSchedulesResponseDtos;
    }

    @NotNull
    private static TimeDto getTime(String start, String end) {
        LocalDateTime startDateTime = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(end).atTime(23, 59);
        return new TimeDto(startDateTime, endDateTime);
    }

}
