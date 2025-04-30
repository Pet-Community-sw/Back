package com.example.PetApp.controller;

import com.example.PetApp.dto.matchpost.CreateMatchPostDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.matchpost.MatchPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matchPosts")
public class MatchPostController {

    private final MatchPostService matchPostService;

    @GetMapping
    private ResponseEntity<?> getMatchPosts(Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return matchPostService.getMatchPosts(profileId);
    }

    @PostMapping
    private ResponseEntity<?> createMatchPost(@RequestBody CreateMatchPostDto createMatchPostDto, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return matchPostService.createMatchPost(createMatchPostDto, profileId);
    }

    @DeleteMapping
    private ResponseEntity<?> deleteMatchPost(@RequestBody Long matchPostId, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return matchPostService.deleteMatchPost(matchPostId, profileId);
    }

    private static Long getProfileId(Authentication authentication) {
        return ((JwtAuthenticationToken) authentication).getProfileId();
    }
}
