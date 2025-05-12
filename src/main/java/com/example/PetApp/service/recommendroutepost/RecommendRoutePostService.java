package com.example.PetApp.service.recommendroutepost;

import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
import com.example.PetApp.dto.recommendroutepost.UpdateRecommendRoutePostDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface RecommendRoutePostService {
    ResponseEntity<?> createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, String email);


    ResponseEntity<?> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email);

    ResponseEntity<?> getRecommendRoutePosts(Double longitude, Double latitude, String email);

    ResponseEntity<?> getRecommendRoutePost(Long recommendRoutePostId, String email);

    ResponseEntity<?> updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, String email);


    ResponseEntity<?> deleteRecommendRoutePost(Long recommendRoutePostId, String email);
}
