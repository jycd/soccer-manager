package com.toptal.soccermanager.controller;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * For endpoints about player
 * 1. User can't create or delete a player
 * 2. User can only read and update the information of players he owns
 */

@RestController
@Validated
@RequestMapping(value = "/teams/{teamId}/players")
public class TeamPlayerController {
    @Autowired
    public PlayerService playerService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PlayerDto> create(@Valid @RequestBody PlayerCreateDto player, @PathVariable("teamId") long teamId) throws ApplicationException {
        PlayerDto newPlayer = playerService.create(player, teamId);

        return new ResponseEntity<>(newPlayer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #teamId == authentication.principal.id)")
    public ResponseEntity<PlayerDto> getById(@PathVariable("id") long id, @PathVariable("teamId") long teamId) throws ApplicationException {
        PlayerDto existPlayer = playerService.getById(id, teamId);

        return ResponseEntity.ok(existPlayer);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #teamId == authentication.principal.id)")
    public ResponseEntity<?> getAll(@Pattern(regexp = "[\\s]*[0-9]*[1-9]+[0-9]*[\\s]*", message = "size must be greater than 0") @RequestParam(required = false, name = "size") String size,
                                    @Pattern(regexp = "[\\s]*[0-9]+[\\s]*", message = "page must be greater than or equal to 0") @RequestParam(required = false, name = "page") String page,
                                    @PathVariable("teamId") long teamId) throws ApplicationException {
        if (size == null && page == null) {
            List<PlayerDto> existPlayers = playerService.getAll(teamId);
            return ResponseEntity.ok(existPlayers);
        } else {
            int pageSize;
            int pageNumber;
            if (size == null) {
                pageSize = 50;
            } else {
                pageSize = Integer.parseInt(size.trim());
            }
            if (page == null) {
                pageNumber = 0;
            } else {
                pageNumber = Integer.parseInt(page.trim());
            }
            if (pageSize > 100) {
                pageSize = 100;
            }
            PagingDataRespDto<PlayerDto> existPlayers = playerService.getAll(new PagingDataReqDto(pageNumber, pageSize), teamId);
            return ResponseEntity.ok(existPlayers);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #teamId == authentication.principal.id)")
    public ResponseEntity<PlayerDto> update(@PathVariable("id") long id, @Valid @RequestBody PlayerUpdateDto player,
                                            @PathVariable("teamId") long teamId) throws ApplicationException {
        PlayerDto updatedPlayer = playerService.update(id, player, teamId);

        return ResponseEntity.ok(updatedPlayer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity delete(@PathVariable("id") long id, @PathVariable("teamId") long teamId) throws ApplicationException {
        playerService.delete(id, teamId);

        return ResponseEntity.noContent().build();
    }
}
