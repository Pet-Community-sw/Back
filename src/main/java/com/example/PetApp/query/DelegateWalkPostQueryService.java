package com.example.PetApp.query;

import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DelegateWalkPostQueryService {

    private final DelegateWalkPostRepository delegateWalkPostRepository;

    public DelegateWalkPost findByDelegateWalkPost(Long postId) {
        return delegateWalkPostRepository.findById(postId).orElseThrow(()->new NotFoundException("해당 대리산책자 게시글은 없습니다."));
    }
}
