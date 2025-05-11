package com.example.PetApp.service.post;

import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.PostListResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    List<PostListResponseDto> getPosts(int page, String email);

    ResponseEntity<?> createPost(PostDto createPostDto, String email);

    ResponseEntity<?> getPost(Long postId, String email);

    ResponseEntity<String> deletePost(Long postId, String email);

    ResponseEntity<?> updatePost(Long postId, PostDto postDto, String email);
}
