package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.WalkingTogetherPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendRoutePostRepository extends JpaRepository<RecommendRoutePost, Long> {
    @Query(value = "select * from RecommendRoutePost r" +
            "where ST_Distance_Sphere(POINT(r.locationLongitude, r.locationLatitude), " +
            "POINT(:longitude, :latitude)) <= 1000 " +
            "order by r.recommendRouteTime desc"
            , nativeQuery = true)
    List<RecommendRoutePost> findByRecommendRoutePostByPlace(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude
    );

    @Query(value = "select * from RecommendRoutePost r " +
            "where r.locationLongitude between :minLongitude and :maxLongitude " +
            "and r.locationLatitude between :minLatitude and :maxLatitude " +
            "order by r.recommendRouteTime desc ",
            nativeQuery = true)
    List<RecommendRoutePost> findByRecommendRoutePostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude
    );
}
