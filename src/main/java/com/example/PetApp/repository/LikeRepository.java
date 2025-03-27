package com.example.PetApp.repository;

import com.example.PetApp.domain.LikeT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeT, Long> {

    Long countByPostId(Long postId);

    Boolean existsByPostIdAndProfileProfileId(Long postId, Long profileId);

    void deleteByPostIdAndProfileProfileId(Long postId, Long profileId);
}
