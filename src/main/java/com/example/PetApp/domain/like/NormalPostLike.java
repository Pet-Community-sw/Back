//package com.example.PetApp.domain.like;
//
//import com.example.PetApp.domain.Member;
//import com.example.PetApp.domain.post.NormalPost;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@DiscriminatorValue("POST")
//@NoArgsConstructor
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
//public class NormalPostLike extends Like {
//
//    @ManyToOne
//    @JoinColumn(name = "normal_post_id", nullable = false)
//    private NormalPost normalPost;
//
//    public NormalPostLike(Member member, NormalPost normalPost) {
//        super(member);
//        this.normalPost = normalPost;
//    }
//
//}
