package com.toptal.soccermanager.utils;

import com.github.javafaker.Faker;
import com.toptal.soccermanager.model.entity.Player;
import com.toptal.soccermanager.model.entity.Position;
import com.toptal.soccermanager.model.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class Creator {
    private static final int AGE_MIN = 18;
    private static final int AGE_MAX = 40;
    private static final int PLAYER_INITIAL_MARKET_VALUE = 1000000;
    private static final int TEAM_INITIAL_BUDGET = 5000000;
    private static final int TEAM_INITIAL_NUMBER_OF_GOALKEEPERS = 3;
    private static final int TEAM_INITIAL_NUMBER_OF_DEFENDERS = 6;
    private static final int TEAM_INITIAL_NUMBER_OF_MIDFIELDERS = 6;
    private static final int TEAM_INITIAL_NUMBER_OF_ATTACKERS = 5;

    private final Faker faker = new Faker();


    public Team createTeam() {
        Team team = new Team();
        team.setName(faker.team().name());
        team.setCountry(faker.country().name());
        team.setBudget(TEAM_INITIAL_BUDGET);
        // add initial goalkeepers
        for (int i = 0; i < TEAM_INITIAL_NUMBER_OF_GOALKEEPERS; i++) {
            team.addPlayer(createGoalKeeper());
        }
        // add initial defenders
        for (int i = 0; i < TEAM_INITIAL_NUMBER_OF_DEFENDERS; i++) {
            team.addPlayer(createDefender());
        }
        // add initial midfielders
        for (int i = 0; i < TEAM_INITIAL_NUMBER_OF_MIDFIELDERS; i++) {
            team.addPlayer(createMidfielder());
        }
        // add initial attackers
        for (int i = 0; i < TEAM_INITIAL_NUMBER_OF_ATTACKERS; i++) {
            team.addPlayer(createAttacker());
        }
        return team;
    }

    public double createMarketValue(double baseValue) {
        return baseValue + baseValue * faker.number().numberBetween(10, 100) / 100;
    }

    private Player createGoalKeeper() {
        return createPlayer(Position.Goalkeeper);
    }

    private Player createDefender() {
        return createPlayer(Position.Defender);
    }

    private Player createMidfielder() {
        return createPlayer(Position.Midfielder);
    }

    private Player createAttacker() {
        return createPlayer(Position.Attacker);
    }

    private Player createPlayer(Position position) {
        Player player = new Player();
        player.setFirstName(faker.name().firstName());
        player.setLastName(faker.name().lastName());
        player.setCountry(faker.country().name());
        player.setAge(faker.number().numberBetween(AGE_MIN, AGE_MAX));
        player.setMarketValue(PLAYER_INITIAL_MARKET_VALUE);
        player.setPosition(position);

        return player;
    }
}
