package com.toptal.soccermanager.repository;

import com.toptal.soccermanager.model.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("select t from Transfer t " +
            "join fetch t.player p " +
            "join fetch p.team where t.id = :id")
    Optional<Transfer> findByIdWithPlayers(long id);

    @Query(value = "select t from Transfer t " +
            "join fetch t.player p " +
            "join fetch p.team",
    countQuery = "select count(t) from Transfer t " +
            "join t.player p " +
            "join p.team")
    Page<Transfer> findAllWithPlayers(Pageable pageable);

    @Query("select t from Transfer t " +
            "join fetch t.player p " +
            "join fetch p.team")
    List<Transfer> findAllWithPlayers();
}
