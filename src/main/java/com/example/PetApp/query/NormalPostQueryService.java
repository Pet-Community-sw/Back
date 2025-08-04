package com.example.PetApp.query;

import com.example.PetApp.domain.post.NormalPost;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.NormalPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalPostQueryService {

    private final NormalPostRepository normalPostRepository;

    public NormalPost findByNormalPost(Long postId) {
        return normalPostRepository.findById(postId).orElseThrow(() -> new NotFoundException("해당 자유 게시물은 없습니다."));
    }
}
