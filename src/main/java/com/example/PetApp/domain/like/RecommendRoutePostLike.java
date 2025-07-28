package com.example.PetApp.domain.like;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RecommendRoutePost;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("RECOMMEND")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecommendRoutePostLike extends Like {


    @ManyToOne
    @JoinColumn(name = "recommend_route_post_id", nullable = false)
    private RecommendRoutePost recommendRoutePost;

}
