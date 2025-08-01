package com.example.PetApp.domain.post;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.like.Like;
import com.example.PetApp.domain.like.NormalPostLike;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("NORMAL")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NormalPost extends Post implements Commentable{

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Override
    public Like createLike(Member member) {
        return new NormalPostLike(member, this);
    }

    @Override
    public List<Comment> getComments() {
        return this.comments;
    }

}
