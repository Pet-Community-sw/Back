package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder//좋아요 어떻게할까
//따로 db에 리스틑 저장안할거임 누른 후 인식만하고 어떤 요청이 있을 때 좋아요 올리기 요청을 보냄?
public class Comment {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_route_post_id")
    private RecommendRoutePost recommendRoutePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime commentTime;
}
