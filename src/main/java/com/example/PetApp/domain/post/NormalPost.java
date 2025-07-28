package com.example.PetApp.domain.post;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.like.Like;
import com.example.PetApp.domain.like.NormalPostLike;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("NORMAL")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NormalPost extends Post{

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String postImageUrl;

    @Setter
    @Min(0)
    @NotNull
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Override
    public Like createLike(Member member) {
        return new NormalPostLike(member, this);
    }
}
