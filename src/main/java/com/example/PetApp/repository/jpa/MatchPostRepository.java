package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.MatchPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.aspectj.weaver.ast.Literal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchPostRepository extends JpaRepository<MatchPost, Long> {
    @Query(value = "select * from match_post m " +
            "where ST_Distance_Sphere(POINT(m.longitude, m.latitude), " +
            "POINT(:longitude, :latitude)) <= 1000 " +
            "order by m.matchPostTiem desc"
            , nativeQuery = true)
    List<MatchPost> findByMatchPostsByPlace(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude
    );

    @Query(value = "select * from match_post m " +
            "where m.longitude between :minLongitude and :maxLongitue " +
            "and m.latitude between :minLatitude and :maxLatitude " +
            "order by m.matchPostTime desc ",
            nativeQuery = true)
    List<MatchPost> findByMatchPostByLocation(
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude
    );
}
