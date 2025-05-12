package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<LikeT, Long> {

    Long countByPost(Post post);

    Long countByRecommendRoutePost(RecommendRoutePost recommendRoutePost);
    List<LikeT> findAllByPost(Post post);

    List<LikeT> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    Boolean existsByPostAndMember(Post post, Member member);

    Boolean existsByRecommendRoutePostAndMember(RecommendRoutePost recommendRoutePost, Member member);

    void deleteByPostAndMember(Post post, Member member);
}
