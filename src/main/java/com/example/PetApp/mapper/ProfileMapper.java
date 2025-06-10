package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.AccessTokenByProfileIdResponseDto;
import com.example.PetApp.dto.profile.GetProfileResponseDto;
import com.example.PetApp.dto.profile.ProfileDto;
import com.example.PetApp.dto.profile.ProfileListResponseDto;
import com.example.PetApp.util.AgeUtil;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

import static com.example.PetApp.util.AgeUtil.CalculateAge;

public class ProfileMapper {
    public static Profile toEntity(ProfileDto profileDto, Member member, String imageFileName) {
        return Profile.builder()
                .member(member)
                .petImageUrl(imageFileName)
                .petBirthDate(profileDto.getPetBirthDate())
                .extraInfo(profileDto.getExtraInfo())
                .petBreed(profileDto.getPetBreed())
                .petAge(AgeUtil.CalculateAge(profileDto.getPetBirthDate())+"살")
                .petName(profileDto.getPetName())
                .build();
    }

    public static GetProfileResponseDto toGetProfileResponseDto(Profile profile, Member member) {
        return GetProfileResponseDto.builder()
                .profileId(profile.getProfileId())
                .petBreed(profile.getPetBreed())
                .petImageUrl(profile.getPetImageUrl())
                .memberId(profile.getMember().getMemberId())
                .petName(profile.getPetName())
                .petAge(profile.getPetAge())
                .petBirthDate(profile.getPetBirthDate())
                .avoidBreeds(profile.getAvoidBreeds())
                .isOwner(member.equals(profile.getMember()))
                .build();
    }

    public static ProfileListResponseDto toProfileListResponseDto(Profile profile) {
        boolean isBirthday = MonthDay.from(LocalDate.now())
                .equals(MonthDay.from(profile.getPetBirthDate()));
        return ProfileListResponseDto.builder()
                .profileId(profile.getProfileId())
                .petImageUrl(profile.getPetImageUrl())
                .petName(profile.getPetName())
                .hasBirthday(isBirthday)
                .build();
    }

    public static AccessTokenByProfileIdResponseDto toAccessTokenToProfileIdResponseDto(Long profileId, String accessToken) {
        return AccessTokenByProfileIdResponseDto.builder()
                .profileId(profileId)
                .accessToken(accessToken)
                .build();
    }

    public static void updateProfile(Profile profile, ProfileDto profileDto, String imageFimeName) {
        profile.setPetImageUrl("/profile/" + imageFimeName);
        profile.setPetName(profileDto.getPetName());
        profile.setPetBirthDate(profileDto.getPetBirthDate());
        profile.setPetAge(CalculateAge(profileDto.getPetBirthDate()) + "살");
        profile.setPetBreed(profileDto.getPetBreed());
        profile.setExtraInfo(profileDto.getExtraInfo());
        profile.getAvoidBreeds().clear();
    }
}
