package com.toptal.soccermanager.repository;

import com.toptal.soccermanager.model.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {
    @Query("select distinct p from Player p " +
            "join fetch p.team pm " +
            "left join fetch p.transfer " +
            "where pm.id = :teamId")
    List<Player> findAllPlayersByTeamId(long teamId);

    @Query(value = "select distinct p from Player p " +
            "join fetch p.team pm " +
            "left join fetch p.transfer " +
            "where pm.id = :teamId",
    countQuery = "select count(distinct p) from Player p " +
            "join p.team pm " +
            "left join p.transfer " +
            "where pm.id = :teamId")
    Page<Player> findAllPlayersByTeamId(long teamId, Pageable pageable);

    @Query("select distinct p from Player p " +
            "left join fetch p.team " +
            "left join fetch p.transfer" )
    List<Player> findAllPlayers();

    @Query(value = "select distinct p from Player p " +
            "left join fetch p.team " +
            "left join fetch p.transfer",
    countQuery = "select count(distinct p) from Player p " +
            "left join p.team " +
            "left join p.transfer")
    Page<Player> findAllPlayers(Pageable pageable);
}
