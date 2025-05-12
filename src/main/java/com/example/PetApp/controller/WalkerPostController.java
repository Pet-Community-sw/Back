package com.example.PetApp.controller;

import com.example.PetApp.dto.walkerpost.CreateWalkerPostDto;
import com.example.PetApp.dto.walkerpost.UpdateWalkerPostDto;
import com.example.PetApp.service.walkerpost.WalkerPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walker-post")
public class WalkerPostController {

    private final WalkerPostService walkerPostService;

    @PostMapping
    private ResponseEntity<?> createWalkerPost(@RequestBody CreateWalkerPostDto createWalkerPostDto, Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.createWalkerPost(createWalkerPostDto, email);
    }

    @PostMapping("/{walkerPostId}")
    private ResponseEntity<?> applyToWalkerPost(@PathVariable Long walkerPostId, Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.applyToWalkerPost(walkerPostId, email);
    }

    @GetMapping("/by-location")
    private ResponseEntity<?> getWalkerPosts(@RequestParam Double minLongitude,
                                            @RequestParam Double minLatitude,
                                            @RequestParam Double maxLongitude,
                                            @RequestParam Double maxLatitude,
                                            Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.getWalkerPostsByLocation(minLongitude, minLatitude, maxLongitude, maxLatitude, email);
    }

    @GetMapping("/by-place")
    private ResponseEntity<?> getWalkerPosts(@RequestParam Double longitude,
                                            @RequestParam Double latitude,
                                            Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.getWalkerPostsByPlace(longitude, latitude, email);
    }

    @GetMapping("/{walkerPostId}")
    private ResponseEntity<?> getMatchPost(@PathVariable Long walkerPostId, Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.getWalkerPost(walkerPostId, email);
    }

    @GetMapping("/{walkerPostId}/applicants")
    private ResponseEntity<?> getApplicants(@PathVariable Long walkerPostId, Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.getApplicants(walkerPostId, email);
    }


    @PutMapping("/{walkerPostId}")
    private ResponseEntity<?> updateMatchPost(@PathVariable Long walkerPostId, @RequestBody UpdateWalkerPostDto updateWalkerPostDto, Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.updateWalkerPost(walkerPostId, updateWalkerPostDto, email);
    }

    @DeleteMapping("/{walkerPostId}")
    private ResponseEntity<?> deleteMatchPost(@PathVariable Long walkerPostId, Authentication authentication) {
        String email = getEmail(authentication);
        return walkerPostService.deleteWalkerPost(walkerPostId, email);
    }


    private static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }
}
