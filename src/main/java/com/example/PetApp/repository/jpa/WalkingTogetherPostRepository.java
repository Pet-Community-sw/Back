package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkingTogetherPostRepository extends JpaRepository<WalkingTogetherMatch, Long> {

    List<WalkingTogetherMatch> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<WalkingTogetherMatch> findAllByProfileContainsAndScheduledTimeBetween(Profile profile, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
