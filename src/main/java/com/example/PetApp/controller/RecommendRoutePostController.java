package com.example.PetApp.controller;

import com.example.PetApp.dto.MessageResponse;
import com.example.PetApp.dto.recommendroutepost.*;
import com.example.PetApp.service.post.recommend.RecommendRoutePostService;
import com.example.PetApp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/recommend-route-posts")
@RequiredArgsConstructor
public class RecommendRoutePostController {

    private final RecommendRoutePostService recommendRoutePostService;

    @PostMapping
    private CreateRecommendRoutePostResponseDto createRecommendRoutePost(@RequestBody @Valid CreateRecommendRoutePostDto createRecommendRoutePostDto,
                                                                         Authentication authentication) {
        return recommendRoutePostService.createRecommendRoutePost(createRecommendRoutePostDto, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/by-location")
    private List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(@RequestParam Double minLongitude,
                                                                           @RequestParam Double minLatitude,
                                                                           @RequestParam Double maxLongitude,
                                                                           @RequestParam Double maxLatitude,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           Authentication authentication) {
        return recommendRoutePostService.getRecommendRoutePosts(minLongitude, minLatitude, maxLongitude, maxLatitude, page, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/by-place")
    private List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(@RequestParam Double longitude,
                                                                           @RequestParam Double latitude,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           Authentication authentication) {
        return recommendRoutePostService.getRecommendRoutePosts(longitude, latitude, page, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{recommendRoutePostId}")
    private GetRecommendPostResponseDto getRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        return recommendRoutePostService.getRecommendRoutePost(recommendRoutePostId, AuthUtil.getEmail(authentication));
    }

    @PutMapping("/{recommendRoutePostId}")
    private ResponseEntity<MessageResponse> updateRecommendRoutePost(@PathVariable Long recommendRoutePostId,
                                                       @RequestBody @Valid UpdateRecommendRoutePostDto updateRecommendRoutePostDto,
                                                       Authentication authentication) {
        recommendRoutePostService.updateRecommendRoutePost(recommendRoutePostId, updateRecommendRoutePostDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{recommendRoutePostId}")
    private ResponseEntity<MessageResponse> deleteRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        recommendRoutePostService.deleteRecommendRoutePost(recommendRoutePostId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }
}
