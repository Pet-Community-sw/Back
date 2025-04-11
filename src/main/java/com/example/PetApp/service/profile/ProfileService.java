package com.example.PetApp.service.profile;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.ProfileDto;
import com.example.PetApp.dto.profile.ProfileListResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProfileService {
    ResponseEntity addProfile(ProfileDto addProfileDto, String email);

    List<ProfileListResponseDto> profileList(String email);

    ResponseEntity getProfile(Long profileId, String email);

    Long getCount(String email);

    ResponseEntity updateProfile(Long profileId, ProfileDto addProfileDto, String email);

    ResponseEntity deleteByProfileId(Long profileId, String email);
}
