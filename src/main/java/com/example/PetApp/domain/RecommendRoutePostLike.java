package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.Like;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendRoutePostLike extends Like {

    @ManyToOne
    @JoinColumn(name = "recommend_route_post_id", nullable = false)
    private RecommendRoutePost recommendRoutePost;
}
