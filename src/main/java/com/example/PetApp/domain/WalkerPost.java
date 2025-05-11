package com.example.PetApp.domain;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkerPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkerPostId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private Long price;

    @NotEmpty
    private Double minLongitude;

    @NotEmpty
    private Double minLatitude;

    @NotEmpty
    private Double maxLongitude;

    @NotEmpty
    private Double maxLatitude;

    @NotEmpty
    private Double locationLongitude;

    @NotEmpty
    private Double locationLatitude;

    private int level;

    @ElementCollection
    @CollectionTable(name = "walker_post_members")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @Builder.Default
    private Set<Long> members=new HashSet<>();


}
