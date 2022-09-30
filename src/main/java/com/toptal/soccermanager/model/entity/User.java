package com.toptal.soccermanager.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user_account")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", length = 60, nullable = false)
    private String fullName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", length = 15, nullable = false)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private Status status;

    @Column(name = "login_attempts", nullable = false)
    private int loginAttempts;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Team team;
}
