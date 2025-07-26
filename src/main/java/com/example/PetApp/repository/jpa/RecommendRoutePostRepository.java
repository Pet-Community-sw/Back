package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.RecommendRoutePost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRoutePostRepository extends JpaRepository<RecommendRoutePost, Long> {
    @Query(value = "select * from recommend_route_post r " +
                    "where ST_Distance_Sphere(POINT(r.location_longitude, r.location_latitude), POINT(:longitude, :latitude)) <= 1000 " +
                    "order by r.recommend_route_time desc ",
            countQuery = "select count(1) from recommend_route_post r " +
                    "where ST_Distance_Sphere(POINT(r.location_longitude, r.location_latitude), POINT(:longitude, :latitude)) <= 1000",
            nativeQuery = true)
    Page<RecommendRoutePost> findByRecommendRoutePostByPlace(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude,
            Pageable pageable
    );


    @Query(value = "select * from recommend_route_post r " +
                    "where r.location_longitude between :minLongitude and :maxLongitude " +
                    "and r.location_latitude between :minLatitude and :maxLatitude " +
                    "order by r.recommend_route_time desc",
            countQuery = "select count(1) from recommend_route_post r " +
                    "where r.location_longitude between :minLongitude and :maxLongitude " +
                    "and r.location_latitude between :minLatitude and :maxLatitude",
            nativeQuery = true)
    Page<RecommendRoutePost> findByRecommendRoutePostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude,
            Pageable pageable
    );

}
