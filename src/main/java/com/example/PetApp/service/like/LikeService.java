package com.example.PetApp.service.like;

import com.example.PetApp.domain.post.Post;
import com.example.PetApp.dto.like.LikeResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LikeService {

    ResponseEntity<?> createAndDeleteLike(Long postId, String email);

    LikeResponseDto getLikes(Long postId);

    <T extends Post> Map<Long, Long> getLikeCountMap(List<T> posts);
}
