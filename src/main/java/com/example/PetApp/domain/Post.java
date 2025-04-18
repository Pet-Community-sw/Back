package com.example.PetApp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    private String postImageUrl;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)//이거 comment에 있어야할듯.
    @JsonIgnore//직렬화 시 안에까지 직렬화 되는건 아님.
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeT> likeTs;

    @CreationTimestamp
    private LocalDateTime regdate;


}
