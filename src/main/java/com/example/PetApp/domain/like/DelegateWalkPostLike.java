package com.example.PetApp.domain.like;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.DelegateWalkPost;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("DELEGATE")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DelegateWalkPostLike extends Like{

    @ManyToOne
    @JoinColumn(name = "delegate_walk_id", nullable = false)
    private DelegateWalkPost delegateWalkPost;

    public DelegateWalkPostLike(Member member, DelegateWalkPost delegateWalkPost) {
        super(member);
        this.delegateWalkPost = delegateWalkPost;
    }
}
