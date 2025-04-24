package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String content;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long likeCount;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;//객체 지향적이 아님. 수정해야될듯.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @CreationTimestamp
    private LocalDateTime commentTime;
}
