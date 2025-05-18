package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.PetApp.domain.Review.*;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMemberAndReviewType(Member member, ReviewType reviewType);

    List<Review> findAllByProfileAndReviewType(Profile profile, ReviewType reviewType);
}
