package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.RecommendRoutePost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DelegateWalkPostRepository extends JpaRepository<DelegateWalkPost, Long> {
    @Query(value = "select * from DelegateWalkPost d " +
            "where ST_Distance_Sphere(POINT(d.locationLongitude, d.locationLatitude), " +
            "POINT(:longitude, :latitude)) <= 1000 " +
            "order by d.scheduledTime desc"
            , nativeQuery = true)
    List<DelegateWalkPost> findByDelegateWalkPostByPlace(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude
    );

    @Query(value = "select * from DelegateWalkPost d" +
            "where d.locationLongitude between :minLongitude and :maxLongitude " +
            "and d.locationLatitude between :minLatitude and :maxLatitude " +
            "order by d.scheduledTime desc ",
            nativeQuery = true)
    List<DelegateWalkPost> findByDelegateWalkPostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude
    );

    List<DelegateWalkPost> findAllBySelectedApplicantMemberIdAndScheduledTimeBetween(Long selectedApplicantMemberId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
