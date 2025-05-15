package com.example.PetApp.controller;

import com.example.PetApp.dto.delegateWalkpost.CreateDelegateWalkPostDto;
import com.example.PetApp.dto.delegateWalkpost.UpdateDelegateWalkPostDto;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.service.walkerpost.DelegateWalkPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delegate-walk-post")
public class DelegateWalkPostController {

    private final DelegateWalkPostService delegateWalkPostService;


    @GetMapping("/check")
    public ResponseEntity<?> checkProfile(Authentication authentication) {
        return delegateWalkPostService.checkProfile(getProfileId(authentication));
    }

    @PostMapping
    public ResponseEntity<?> createDelegateWalkPost(@RequestBody CreateDelegateWalkPostDto createDelegateWalkPostDto, Authentication authentication) {
        return delegateWalkPostService.createDelegateWalkPost(createDelegateWalkPostDto, getProfileId(authentication));
    }

    @PostMapping("/{delegateWalkPostId}")
    public ResponseEntity<?> applyToWalkerPost(@PathVariable Long delegateWalkPostId,
                                               @RequestBody String content,
                                               Authentication authentication) throws JsonProcessingException {
        return delegateWalkPostService.applyToDelegateWalkPost(delegateWalkPostId, content, getEmail(authentication));
    }

    @GetMapping("/by-location")
    public ResponseEntity<?> getWalkerPosts(@RequestParam Double minLongitude,
                                            @RequestParam Double minLatitude,
                                            @RequestParam Double maxLongitude,
                                            @RequestParam Double maxLatitude,
                                            Authentication authentication) {
        String email = getEmail(authentication);
        return delegateWalkPostService.getDelegateWalkPostsByLocation(minLongitude, minLatitude, maxLongitude, maxLatitude, email);
    }

    @GetMapping("/by-place")
    public ResponseEntity<?> getWalkerPosts(@RequestParam Double longitude,
                                            @RequestParam Double latitude,
                                            Authentication authentication) {
        String email = getEmail(authentication);
        return delegateWalkPostService.getDelegateWalkPostsByPlace(longitude, latitude, email);
    }

    @GetMapping("/{delegateWalkPostId}")
    public ResponseEntity<?> getMatchPost(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        String email = getEmail(authentication);
        return delegateWalkPostService.getDelegateWalkPost(delegateWalkPostId, email);
    }

    @GetMapping("/{delegateWalkPostId}/applicants")
    public ResponseEntity<?> getApplicants(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        return delegateWalkPostService.getApplicants(delegateWalkPostId, getProfileId(authentication));
    }


    @PutMapping("/{delegateWalkPostId}")
    public ResponseEntity<?> updateMatchPost(@PathVariable Long delegateWalkPostId, @RequestBody UpdateDelegateWalkPostDto updateDelegateWalkPostDto, Authentication authentication) {
        String email = getEmail(authentication);
        return delegateWalkPostService.updateDelegateWalkPost(delegateWalkPostId, updateDelegateWalkPostDto, email);
    }

    @DeleteMapping("/{delegateWalkPostId}")
    public ResponseEntity<?> deleteMatchPost(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        String email = getEmail(authentication);
        return delegateWalkPostService.deleteDelegateWalkPost(delegateWalkPostId, email);
    }


    private static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    private static Long getProfileId(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return jwtAuthenticationToken.getProfileId();
    }
}
