package com.example.PetApp.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "likeT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @JoinColumn(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    //상속을 이용하여 comment 좋아요도 받아보자.
    //mappedsuperclass 이용하여 regdate 및 created를 설정 해보자

}
