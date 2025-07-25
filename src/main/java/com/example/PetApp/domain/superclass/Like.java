package com.example.PetApp.domain.superclass;

import com.example.PetApp.domain.Member;
import lombok.Getter;

import javax.persistence.*;

@MappedSuperclass
@Getter
public abstract class Like extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;
}
