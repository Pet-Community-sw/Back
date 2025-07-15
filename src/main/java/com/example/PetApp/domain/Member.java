package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//JPA 내부에서는 접근 가능하고, 외부에서는 new로 빈 객체 생성 못 하게
//기본 생성자를 protected로 두는 게 안전하고 객체지향적이다
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "memberId", callSuper = false) //엔티티의 동등/중복 판단은 memberId(유일한값)으로 판단하기 위함.
/*
* 부모 클래스의 필드를 equals/hashCode에 포함하고 싶다면
 @EqualsAndHashCode(callSuper = true) 사용
자식 클래스의 필드만 비교하고 싶다면
 @EqualsAndHashCode(callSuper = false) 사용
* */
public class Member extends BaseTimeEntity {//수정 필요

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String email;

    @Setter
    @JsonIgnore//중요한 정보 숨김. 반환 값에 넣어도 반환이 안됨.
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private String memberImageUrl;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private FcmToken fcmToken;

    @Builder.Default
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts=new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecommendRoutePost> recommendRoutePosts=new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Profile> profiles = new ArrayList<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "member_role",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }
}
