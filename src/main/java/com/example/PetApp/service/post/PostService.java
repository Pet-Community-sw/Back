package com.example.PetApp.service.post;

import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    List<PostListResponseDto> getPosts(int page);

    ResponseEntity<Object> createPost(PostDto createPostDto, Long profileId);

    ResponseEntity<Object> getPost(Long postId, Long profileId);

    ResponseEntity<String> deletePost(Long postId, String email);

    ResponseEntity<Object> updatePost(Long postId, PostDto postDto, Long profileId, String email);
}
