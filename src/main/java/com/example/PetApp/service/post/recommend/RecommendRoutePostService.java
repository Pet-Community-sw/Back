package com.example.PetApp.service.post.recommend;

import com.example.PetApp.dto.recommendroutepost.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecommendRoutePostService {
    CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, String email);

    List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email);

    List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, String email);

    GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, String email);

    void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, String email);


    void deleteRecommendRoutePost(Long recommendRoutePostId, String email);
}
