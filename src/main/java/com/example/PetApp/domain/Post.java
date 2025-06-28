package com.example.PetApp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String title;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String content;

    @NotBlank
    @Column(nullable = false)
    private String postImageUrl;

    @Min(0)
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)//이거 comment에 있어야할듯.
    @JsonIgnore//직렬화 시 안에까지 직렬화 되는건 아님.
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeT> likeTs = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime postTime;

}
