package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.*;
import com.example.PetApp.domain.like.Like;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.dto.like.LikeCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
//프로젝션을 통해 한 번 번경해보자
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select new com.example.PetApp.dto.like.LikeCountDto(l.post.postId, count(1))" +
            "from Like l where l.post in :posts " +
            "group by l.post.postId")
    List<LikeCountDto> countByPosts(@Param("posts") List<Post> posts);

    @Query("select l.post.postId from Like l where l.member = :member AND l.post in :posts")
    List<Long> findLikedPostIds(@Param("member") Member member, @Param("posts") List<Post> posts);

    @Query("select new com.example.PetApp.dto.like.LikeCountDto(l.recommendRoutePost.recommendRouteId, count(1))" +
            "from Like l where l.recommendRoutePost in :recommendRoutePost " +
            "group by l.recommendRoutePost.recommendRouteId")
    List<LikeCountDto> countByRecommendRoutePost(@Param("recommendRoutePost") List<RecommendRoutePost> recommendRoutePosts);

    @Query("select l.recommendRoutePost.recommendRouteId from Like l where l.member=:member and l.recommendRoutePost in :recommendRoutePosts")
    List<Long> findLikedRecommendIds(@Param("member") Member member, @Param("recommendRoutePosts") List<RecommendRoutePost> recommendRoutePosts);

    @Query(value = "select count(*) from like where post_id = :postId", nativeQuery = true)
    Long countByPost(@Param("postId")Long postId);

//    Long countByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

//    List<Like> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);
//
//    List<Like> findAllByPost(Post post);

    Boolean existsByPostAndMember(Post post, Member member);

    Boolean existsByRecommendRoutePostAndMember(RecommendRoutePost recommendRoutePost, Member member);

//    void deleteByRecommendRoutePostAndMember(RecommendRoutePost recommendRoutePost, Member member);
//
//    void deleteByPostAndMember(Post post, Member member);
}
