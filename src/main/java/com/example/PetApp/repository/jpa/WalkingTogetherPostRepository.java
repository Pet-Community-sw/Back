package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkingTogetherPostRepository extends JpaRepository<WalkingTogetherPost, Long> {

    List<WalkingTogetherPost> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<WalkingTogetherPost> findAllByProfileContainsAndScheduledTimeBetween(Profile profile, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
