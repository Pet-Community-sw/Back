package com.example.PetApp.domain.post;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.WalkingTogetherMatch;
import com.example.PetApp.domain.embedded.Location;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("RECOMMEND")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class RecommendRoutePost extends Post{

    @Embedded
    private Location location;

    @OneToMany(mappedBy = "recommendRoutePost",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkingTogetherMatch> walkingTogetherMatch;

//    @OneToMany(mappedBy = "recommendRoutePost", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<LikeT> likeTs;

    @OneToMany(mappedBy = "recommendRoutePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
