package com.example.PetApp.domain;

import com.example.PetApp.domain.embedded.Location;
import com.example.PetApp.domain.superclass.BaseTimeEntity;
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
@Builder
public class RecommendRoutePost extends BaseTimeEntity {

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

    @Embedded
    private Location location;

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
