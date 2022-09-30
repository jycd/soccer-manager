package com.toptal.soccermanager.controller;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * User can update transfer ask prices for only the players of his team.
 */

@RestController
@RequestMapping(value = "/teams/{teamId}/transfers")
public class TeamTransferController {
    @Autowired
    private TransferService transferService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #teamId == authentication.principal.id)")
    public ResponseEntity<TransferDto> create(@Valid @RequestBody TransferCreateDto transfer, @PathVariable("teamId") long teamId) throws ApplicationException {
        TransferDto newTransfer = transferService.create(transfer, teamId);

        return new ResponseEntity<>(newTransfer, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #teamId == authentication.principal.id)")
    public ResponseEntity<TransferDto> update(@PathVariable("id") long id, @Valid @RequestBody TransferUpdateDto transfer,
                                              @PathVariable("teamId") long teamId) throws ApplicationException {
        TransferDto updatedTransfer = transferService.update(id, transfer, teamId);

        return ResponseEntity.ok(updatedTransfer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #teamId == authentication.principal.id)")
    public ResponseEntity delete(@PathVariable("id") long id, @PathVariable("teamId") long teamId) throws ApplicationException {
        transferService.delete(id, teamId);

        return ResponseEntity.noContent().build();
    }
}
