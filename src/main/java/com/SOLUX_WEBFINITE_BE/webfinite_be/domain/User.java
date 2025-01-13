package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_user_id", nullable = false, unique = true)
    private String loginUserId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "confirm_password", nullable = false)
    private String confirmPassword;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Course는 일대다 관계
    private List<Course> courses = new ArrayList<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Attend는 일대다 관계
//    private List<Attend> attends = new ArrayList<>();

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL) // User와 UserProfile는 일대일 관계
//    private List<UserProfile> userProfiles = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Todo는 일대다 관계
//    private List<Todo> todos = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 UserAnswer는 일대다 관계
//    private List<UserAnswer> userAnswers = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // User와 Quiz는 일대다 관계
//    private List<Quiz> quizzes = new ArrayList<>();

}
