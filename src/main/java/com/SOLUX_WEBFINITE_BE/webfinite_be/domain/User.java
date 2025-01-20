package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_user_id", nullable = false, unique = true)
    private String loginUserId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "confirm_password", nullable = false)
    private String confirmPassword;

    @Column(name = "plain_password", nullable = false)
    private String plainPassword; // 평문 비밀번호 (비밀번호 찾기 기능을 위함)

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Course는 일대다 관계
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Attend는 일대다 관계
    private List<Attend> attends = new ArrayList<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Todo는 일대다 관계
//    private List<Todo> todos = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 UserAnswer는 일대다 관계
//    private List<UserAnswer> userAnswers = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Quiz는 일대다 관계
//    private List<Quiz> quizzes = new ArrayList<>();
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL) // User와 UserProfile는 일대일 관계
    private UserProfile userProfile;

    // == 비즈니스 로직 == //
    public void setPassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // == 로그인 시 사용 == //
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return this.loginUserId; // 로그인 식별자로 사용
    }
}
