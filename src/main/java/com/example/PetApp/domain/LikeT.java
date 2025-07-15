package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class LikeT extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_route_post_id")
    private RecommendRoutePost recommendRoutePost;

    @ManyToOne(fetch = FetchType.LAZY)//@OneToOne관계 아님?
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    //상속을 이용하여 comment 좋아요도 받아보자.
    //mappedsuperclass 이용하여 regdate 및 created를 설정 해보자

}
