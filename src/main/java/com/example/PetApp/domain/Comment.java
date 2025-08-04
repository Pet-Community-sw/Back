package com.example.PetApp.domain;

import com.example.PetApp.domain.post.NormalPost;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder//좋아요 어떻게할까
//따로 db에 리스틑 저장안할거임 누른 후 인식만하고 어떤 요청이 있을 때 좋아요 올리기 요청을 보냄?
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String content;

    @Min(0)
    @Setter
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long likeCount;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}

