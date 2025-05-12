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
    @Query(value = "select * from match_post m " +
            "where ST_Distance_Sphere(POINT(m.longitude, m.latitude), " +
            "POINT(:longitude, :latitude)) <= 1000 " +
            "order by m.matchPostTiem desc"
            , nativeQuery = true)
    List<RecommendRoutePost> findByRecommendRoutePostByPlace(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude
    );

    @Query(value = "select * from match_post m " +
            "where m.longitude between :minLongitude and :maxLongitude " +
            "and m.latitude between :minLatitude and :maxLatitude " +
            "order by m.matchPostTime desc ",
            nativeQuery = true)
    List<RecommendRoutePost> findByRecommendRoutePostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude
    );
}
