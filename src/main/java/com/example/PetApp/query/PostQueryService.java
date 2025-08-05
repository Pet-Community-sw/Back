package com.example.PetApp.query;

import com.example.PetApp.domain.post.Post;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;

    public Post findByPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
    }
}
