package com.example.PetApp.controller;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.AddProfileDto;
import com.example.PetApp.dto.profile.AddProfileResponseDto;
import com.example.PetApp.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<AddProfileResponseDto> createProfile(@ModelAttribute AddProfileDto addProfileDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        Profile profile = profileService.addProfile(addProfileDto,email);
        AddProfileResponseDto addProfileResponseDto = AddProfileResponseDto.builder()
                .profileId(profile.getProfileId())
                .dogBreed(profile.getDogBreed())
                .name(profile.getName())
                .imageUrl(profile.getImageUrl())
                .memberId(profile.getMember().getMemberId())
                .build();

        return ResponseEntity.ok(addProfileResponseDto);
    }
}
