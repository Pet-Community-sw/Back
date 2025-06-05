package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.like.LikeCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeT, Long> {

    @Query("select new com.example.PetApp.dto.like.LikeCountDto(l.post.postId, count(1))" +
            "from LikeT l where l.post in :posts " +
            "group by l.post.postId")
    List<LikeCountDto> countByPosts(@Param("posts") List<Post> posts);

    @Query("select l.post.postId from LikeT l where l.member = :member AND l.post in :posts")
    List<Long> findLikedPostIds(@Param("member") Member member, @Param("posts") List<Post> posts);

    @Query("select new com.example.PetApp.dto.like.LikeCountDto(l.recommendRoutePost.recommendRouteId, count(1))" +
            "from LikeT l where l.recommendRoutePost in :recommendRoutePost " +
            "group by l.recommendRoutePost.recommendRouteId")
    List<LikeCountDto> countByRecommendRoutePost(@Param("recommendRoutePost") List<RecommendRoutePost> recommendRoutePosts);

    @Query("select l.recommendRoutePost.recommendRouteId from LikeT l where l.member=:member and l.recommendRoutePost in :recommendRoutePosts")
    List<Long> findLikedRecommendIds(@Param("member") Member member, @Param("recommendRoutePosts") List<RecommendRoutePost> recommendRoutePosts);

    Long countByPost(Post post);

    Long countByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<LikeT> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<LikeT> findAllByPost(Post post);

    Boolean existsByPostAndMember(Post post, Member member);

    Boolean existsByRecommendRoutePostAndMember(RecommendRoutePost recommendRoutePost, Member member);

    void deleteByRecommendRoutePostAndMember(RecommendRoutePost recommendRoutePost, Member member);

    void deleteByPostAndMember(Post post, Member member);
}
