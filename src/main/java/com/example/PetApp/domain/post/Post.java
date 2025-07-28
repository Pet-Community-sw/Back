package com.example.PetApp.domain.post;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.domain.like.Like;
import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DiscriminatorColumn(name = "post_type")
@Builder
@Getter
public abstract class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Embedded
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    public abstract Like createLike(Member member);//게시글에서 Like생성 책임 위임

}
