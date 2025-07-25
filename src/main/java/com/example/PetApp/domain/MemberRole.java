package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "member_role")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MemberRole extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberRoleId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
