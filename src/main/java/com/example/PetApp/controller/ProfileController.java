package com.example.PetApp.controller;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.ProfileDto;
import com.example.PetApp.dto.profile.ProfileListResponseDto;
import com.example.PetApp.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity profileList(Authentication authentication) {
        String email = getEmail(authentication);
        List<ProfileListResponseDto> list = profileService.profileList(email);
        return ResponseEntity.ok().body(list);//dogBreed를 안내보내도 될듯? 효빈이랑 얘기해봐야됨.
    }

    @GetMapping("/{profileId}")
    public ResponseEntity getProfile(@PathVariable Long profileId, Authentication authentication) {
        String email = getEmail(authentication);
        return profileService.getProfile(profileId, email);
    }

    @PostMapping
    public ResponseEntity createProfile(@ModelAttribute ProfileDto profileDto, Authentication authentication) {
        String email = getEmail(authentication);
        Long count = profileService.getCount(email);
        if (count == 4) {
            return ResponseEntity.badRequest().body("프로필은 최대 4개 입니다.");
        }
        return profileService.addProfile(profileDto, email);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity updateProfile(@PathVariable Long profileId, @ModelAttribute ProfileDto addProfileDto, Authentication authentication) {
        String email = getEmail(authentication);
        return profileService.updateProfile(profileId, addProfileDto, email);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity deleteProfile(@PathVariable Long profileId, Authentication authentication) {
        String email = getEmail(authentication);
        return profileService.deleteByProfileId(profileId, email);
    }

    private static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

}
