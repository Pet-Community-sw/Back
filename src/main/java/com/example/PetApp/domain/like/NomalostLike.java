package com.example.PetApp.domain.like;

import com.example.PetApp.domain.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@DiscriminatorValue("POST")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NomalostLike extends Like {

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

}
