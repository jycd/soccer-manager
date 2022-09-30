package com.toptal.soccermanager.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "team")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @Column(name = "user_id")
    private long id;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "country", length = 60, nullable = false)
    private String country;

    @Column(name = "budget", nullable = false)
    private double budget;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Player> players = new HashSet<>();

    public double getMarketValue() {
        return players.stream().mapToDouble(Player::getMarketValue).sum();
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.setTeam(this);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setTeam(null);
    }
}
