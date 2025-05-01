package com.example.PetApp.controller;

import com.example.PetApp.dto.matchpost.CreateMatchPostDto;
import com.example.PetApp.dto.matchpost.UpdateMatchPostDto;
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

    @GetMapping("/by-location")
    private ResponseEntity<?> getMatchPosts(@RequestParam Double minLongitude,
                                            @RequestParam Double minLatitude,
                                            @RequestParam Double maxLongitude,
                                            @RequestParam Double maxLatitude,
                                            Authentication authentication) {

        Long profileId = getProfileId(authentication);
        return matchPostService.getMatchPostsByLocation(minLongitude, minLatitude, maxLongitude, maxLatitude, profileId);
    }

    @GetMapping("/by-place")
    private ResponseEntity<?> getMatchPosts(@RequestParam Double longitude,
                                            @RequestParam Double latitude,
                                            Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return matchPostService.getMatchPostsByPlace(longitude, latitude, profileId);
    }

    @GetMapping("/{matchPostId}")
    private ResponseEntity<?> getMatchPost(@PathVariable Long matchPostId, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return matchPostService.getMatchPost(matchPostId, profileId);
    }

    @PostMapping
    private ResponseEntity<?> createMatchPost(@RequestBody CreateMatchPostDto createMatchPostDto, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return matchPostService.createMatchPost(createMatchPostDto, profileId);
    }

    @PutMapping
    private ResponseEntity<?> updateMatchPost(@RequestBody UpdateMatchPostDto updateMatchPostDto, Authentication authentication) {
        Long profileId = getProfileId(authentication);
        return matchPostService.updateMatchPost(updateMatchPostDto, profileId);
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
