package com.example.PetApp.domain.post;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.domain.embedded.Location;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("RECOMMEND")
@PrimaryKeyJoinColumn(name = "post_id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SuperBuilder//상속받은 필드를 사용하기위한 애노테이션
public class RecommendRoutePost extends Post implements Commentable{

    @Embedded
    private Location location;

    @OneToMany(mappedBy = "recommendRoutePost",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherPost> walkingTogetherPosts;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Override
    public List<Comment> getComments() {
        return this.comments;
    }

//    @Override
//    public Like createLike(Member member) {
//        return new RecommendRoutePostLike(member, this);
//    }
}
