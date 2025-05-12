package com.example.PetApp.controller;

import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.UpdateWalkingTogetherPostDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.matchpost.WalkingTogetherPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walking-together-posts")
public class WalkingTogetherPostController {

    private final WalkingTogetherPostService walkingTogetherPostService;


    @GetMapping("/{walkingTogetherPostId}")
    private ResponseEntity<?> getWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return walkingTogetherPostService.getWalkingTogetherPost(walkingTogetherPostId, profileId);
    }

    @GetMapping("/by-recommend-route-post/{recommendRoutePostId}")
    private ResponseEntity<?> getWalkingTogetherPostsList(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return walkingTogetherPostService.getWalkingTogetherPostsList(recommendRoutePostId, profileId);
    }

    @PostMapping
    private ResponseEntity<?> createWalkingTogetherPost(@RequestBody CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return walkingTogetherPostService.createWalkingTogetherPost(createWalkingTogetherPostDto, profileId);
    }

    @PutMapping("/{walkingTogetherPostId}")
    private ResponseEntity<?> updateWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, @RequestBody UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return walkingTogetherPostService.updateWalkingTogetherPost(walkingTogetherPostId, updateWalkingTogetherPostDto, profileId);
    }

    @DeleteMapping("/{walkingTogetherPostId}")
    private ResponseEntity<?> deleteWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return walkingTogetherPostService.deleteWalkingTogetherPost(walkingTogetherPostId, profileId);
    }

    @PostMapping("/{walkingTogetherPostId}")
    private ResponseEntity<?> startMatch(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return walkingTogetherPostService.startMatch(walkingTogetherPostId, profileId);
    }

    private static Long getProfileId(Authentication authentication) {
        return ((JwtAuthenticationToken) authentication).getProfileId();
    }
}
