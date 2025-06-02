package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.*;
import com.example.PetApp.dto.like.LikeCountDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<LikeT, Long> {

    @Query("select new com.example.PetApp.dto.like.LikeResponseDto(l.post.postId, count(1))" +
            "from LikeT l where l.post in :posts " +
            "group by l.post.postId")
    List<LikeCountDto> countByPosts(@Param("posts") List<Post> posts);

    @Query("SELECT l.post.postId FROM LikeT l WHERE l.member = :member AND l.post IN :posts")
    List<Long> findLikedPostIds(@Param("member") Member member, @Param("posts") List<Post> posts);


    Long countByPost(Post post);

    Long countByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<LikeT> findAllByRecommendRoutePost(RecommendRoutePost recommendRoutePost);

    List<LikeT> findAllByPost(Post post);

    Boolean existsByPostAndMember(Post post, Member member);

    Boolean existsByRecommendRoutePostAndMember(RecommendRoutePost recommendRoutePost, Member member);

    void deleteByRecommendRoutePostAndMember(RecommendRoutePost recommendRoutePost, Member member);

    void deleteByPostAndMember(Post post, Member member);
}
