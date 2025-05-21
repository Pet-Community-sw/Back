package com.example.PetApp.controller;

import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
import com.example.PetApp.dto.recommendroutepost.UpdateRecommendRoutePostDto;
import com.example.PetApp.service.recommendroutepost.RecommendRoutePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend-route-posts")
@RequiredArgsConstructor
public class RecommendRoutePostController {

    private final RecommendRoutePostService recommendRoutePostService;

    @PostMapping
    private ResponseEntity<?> createRecommendRoutePost(@RequestBody CreateRecommendRoutePostDto createRecommendRoutePostDto,
                                                       Authentication authentication) {
        String email = getEmail(authentication);
        return recommendRoutePostService.createRecommendRoutePost(createRecommendRoutePostDto, email);
    }

    @GetMapping("/by-location")
    private ResponseEntity<?> getRecommendRoutePosts(@RequestParam Double minLongitude,
                                            @RequestParam Double minLatitude,
                                            @RequestParam Double maxLongitude,
                                            @RequestParam Double maxLatitude,
                                            Authentication authentication) {

        String email = getEmail(authentication);
        return recommendRoutePostService.getRecommendRoutePosts(minLongitude, minLatitude, maxLongitude, maxLatitude, email);
    }

    @GetMapping("/by-place")
    private ResponseEntity<?> getRecommendRoutePosts(@RequestParam Double longitude,
                                            @RequestParam Double latitude,
                                            Authentication authentication) {
        String email = getEmail(authentication);
        return recommendRoutePostService.getRecommendRoutePosts(longitude, latitude, email);
    }

    @GetMapping("/{recommendRoutePostId}")
    private ResponseEntity<?> getRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        String email = getEmail(authentication);
        return recommendRoutePostService.getRecommendRoutePost(recommendRoutePostId, email);
    }

    @PutMapping("/{recommendRoutePostId}")
    private ResponseEntity<?> updateRecommendRoutePost(@PathVariable Long recommendRoutePostId,
                                                       @RequestBody UpdateRecommendRoutePostDto updateRecommendRoutePostDto,
                                                       Authentication authentication) {
        String email = getEmail(authentication);
        return recommendRoutePostService.updateRecommendRoutePost(recommendRoutePostId, updateRecommendRoutePostDto, email);
    }

    @DeleteMapping("/{recommendRoutePostId}")
    private ResponseEntity<?> deleteRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        String email = getEmail(authentication);
        return recommendRoutePostService.deleteRecommendRoutePost(recommendRoutePostId, email);
    }


    private static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }
}
