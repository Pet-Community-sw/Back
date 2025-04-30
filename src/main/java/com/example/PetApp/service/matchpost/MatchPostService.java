package com.example.PetApp.service.matchpost;

import com.example.PetApp.dto.matchpost.CreateMatchPostDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MatchPostService {


    ResponseEntity<?> getMatchPosts(Long profileId);

    ResponseEntity<?> createMatchPost(CreateMatchPostDto createMatchPostDto, Long profileId);

    ResponseEntity<?> deleteMatchPost(Long matchPostId, Long profileId);
}
