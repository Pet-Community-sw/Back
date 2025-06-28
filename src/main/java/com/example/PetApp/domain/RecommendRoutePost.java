package com.example.PetApp.domain;

import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class RecommendRoutePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendRouteId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String title;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String content;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Double locationLongitude;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Double locationLatitude;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime recommendRouteTime;

    @OneToMany(mappedBy = "recommendRoutePost",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherPost> walkingTogetherPost;

    @OneToMany(mappedBy = "recommendRoutePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeT> likeTs;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "recommendRoutePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

}
