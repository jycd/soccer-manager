package com.toptal.soccermanager.controller;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * For endpoints about teams.
 * 1. User can't create or delete a team.
 * 2. User can only read and update his own team information.
 */

@RestController
@Validated
@RequestMapping(value = "/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #id == authentication.principal.id)")
    public ResponseEntity<TeamDto> getById(@PathVariable("id") long id,
                                           @RequestParam(required = false, name = "with_players", defaultValue = "false") boolean withPlayers) throws ApplicationException {
        TeamDto existTeam;
        if (withPlayers) {
            existTeam = teamService.getByIdWithPlayers(id);
        } else {
            existTeam = teamService.getByIdWithoutPlayers(id);
        }

        return ResponseEntity.ok(existTeam);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAll(@Pattern(regexp = "[\\s]*[0-9]*[1-9]+[0-9]*[\\s]*", message = "size must be greater than 0") @RequestParam(required = false, name = "size") String size,
                                    @Pattern(regexp = "[\\s]*[0-9]+[\\s]*", message = "page must be greater than or equal to 0") @RequestParam(required = false, name = "page") String page) throws ApplicationException {
        if (size == null && page == null) {
            List<TeamDto> existTeams = teamService.getAll();
            return ResponseEntity.ok(existTeams);
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
            PagingDataRespDto<TeamDto> existTeams = teamService.getAll(new PagingDataReqDto(pageNumber, pageSize));
            return ResponseEntity.ok(existTeams);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #id == authentication.principal.id)")
    public ResponseEntity<TeamDto> update(@PathVariable("id") long id, @Valid @RequestBody TeamUpdateDto team) throws ApplicationException {
        TeamDto updatedTeam = teamService.update(id, team);

        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity delete(@PathVariable("id") long id) throws ApplicationException {
        teamService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
