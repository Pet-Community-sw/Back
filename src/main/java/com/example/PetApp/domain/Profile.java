package com.example.PetApp.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "profile")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @NotEmpty
    private String imageUrl;

    @NotEmpty
    private String dogBreed;

    @NotEmpty
    private String name;

    @JoinColumn(name = "member_id")
    private Long memberId;
}
