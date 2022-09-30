package com.toptal.soccermanager.repository;

import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.utils.TeamPair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select t from Team t " +
            "left join fetch t.players p " +
            "left join fetch p.transfer " +
            "where t.id = :id")
    Optional<Team> findByIdWithPlayers(long id);

    @Query("select new com.toptal.soccermanager.utils.TeamPair(t, sum(p.marketValue)) " +
            "from Team t " +
            "left join t.players p " +
            "group by t.id " +
            "having t.id = :id")
    /*@Query(value = "select t as team, sum(p.market_value) as marketValue from team t " +
            "left join player p on t.user_id = p.team_id " +
            "group by t.user_id having t.user_id = :id", nativeQuery = true)*/
    Optional<TeamPair> findByIdWithoutPlayers(long id);

    @Query("select t, sum(p.marketValue) from Team t left join t.players p")
    Page<TeamPair> findAllWithoutPlayers(Pageable pageable);
}
