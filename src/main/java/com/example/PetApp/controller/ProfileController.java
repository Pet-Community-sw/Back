package com.example.PetApp.controller;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.dto.profile.AddProfileDto;
import com.example.PetApp.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//전체 몇개인지도 알아야 됨. 프로필 갯수 최대 몇개
@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity profileList(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        List<Profile> list = profileService.profileList(email);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity getProfile(@PathVariable Long profileId) {
        return profileService.getProfile(profileId);
    }

    @PostMapping
    public ResponseEntity createProfile(@ModelAttribute AddProfileDto addProfileDto, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        Long count = profileService.getCount(email);
        if (count == 4) {
            return ResponseEntity.badRequest().body("프로필은 최대 4개 입니다.");
        }
        Profile profile = profileService.addProfile(addProfileDto, email);
        return ResponseEntity.ok(profile);
    }



}
