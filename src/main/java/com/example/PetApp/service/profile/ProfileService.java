package com.example.PetApp.service.profile;

import com.example.PetApp.dto.profile.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProfileService {
    CreateProfileResponseDto createProfile(ProfileDto addProfileDto, String email);

    List<ProfileListResponseDto> getProfiles(String email);

    GetProfileResponseDto getProfile(Long profileId, String email);

    void updateProfile(Long profileId, ProfileDto addProfileDto, String email);

    void deleteProfile(Long profileId, String email);

    AccessTokenByProfileIdResponseDto accessTokenByProfile(String accessToken, String refreshToken, Long profileId, String email);
}
