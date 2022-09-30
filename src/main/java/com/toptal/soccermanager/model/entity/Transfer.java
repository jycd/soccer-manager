package com.toptal.soccermanager.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "transfer")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    @Id
    @Column(name = "player_id")
    private long id;

    @Column(name = "ask_price", nullable = false)
    private double askPrice;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;
}