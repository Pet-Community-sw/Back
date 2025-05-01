package com.example.PetApp.service.matchpost;

import com.example.PetApp.dto.matchpost.CreateMatchPostDto;
import com.example.PetApp.dto.matchpost.UpdateMatchPostDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MatchPostService {


    ResponseEntity<?> getMatchPostsByPlace(Double longitude, Double latitude, Long profileId);
    ResponseEntity<?> createMatchPost(CreateMatchPostDto createMatchPostDto, Long profileId);

    ResponseEntity<?> deleteMatchPost(Long matchPostId, Long profileId);

    ResponseEntity<?> getMatchPost(Long matchPostId, Long profileId);

    ResponseEntity<?> updateMatchPost(UpdateMatchPostDto updateMatchPostDto, Long profileId);

    ResponseEntity<?> getMatchPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, Long profileId);


}
