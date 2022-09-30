package com.toptal.soccermanager.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "player")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @Column(name = "country", length = 60, nullable = false)
    private String country;

    @Column(name = "age", nullable = false)
    private int age;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "position", length = 10, nullable = false)
    private Position position;

    @Column(name = "market_value", nullable = false)
    private double marketValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Transfer transfer;

    public User getUser() {
        if (team != null && team.getUser() != null) {
            return team.getUser();
        }
        return null;
    }
}
