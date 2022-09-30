package com.toptal.soccermanager.service.impl;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.configuration.exception.ApplicationError;
import com.toptal.soccermanager.model.dto.*;
import com.toptal.soccermanager.model.entity.Player;
import com.toptal.soccermanager.model.entity.Team;
import com.toptal.soccermanager.model.entity.Transfer;
import com.toptal.soccermanager.repository.PlayerRepository;
import com.toptal.soccermanager.repository.TeamRepository;
import com.toptal.soccermanager.repository.TransferRepository;
import com.toptal.soccermanager.service.TransferService;
import com.toptal.soccermanager.utils.Creator;
import com.toptal.soccermanager.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransferServiceImpl implements TransferService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private Creator creator;

    @Autowired
    private Mapper mapper;

    @Override
    @Transactional
    public TransferDto create(TransferCreateDto transfer, Long teamId) throws ApplicationException {
        Player existPlayer = playerRepository.findById(transfer.getPlayerId())
                .orElseThrow(() -> new ApplicationException(ApplicationError.PLAYER_NOT_FOUND));

        if (teamId != null && (existPlayer.getTeam() == null || existPlayer.getTeam().getId() != teamId)) {
            throw new ApplicationException(ApplicationError.UNAUTHORIZED_USER_ERROR);
        }

        Optional<Transfer> existTransfer = transferRepository.findById(transfer.getPlayerId());
        if (existTransfer.isPresent()) {
            throw new ApplicationException(ApplicationError.TRANSFER_DUPLICATED);
        }

        Transfer newTransfer = new Transfer();
        newTransfer.setAskPrice(transfer.getAskPrice());
        newTransfer.setPlayer(existPlayer);
        Transfer createdTransfer = transferRepository.save(newTransfer);

        return mapper.getTransferDtoFromEntity(createdTransfer, true);
    }

    @Override
    @Transactional
    public TransferDto getById(long id) throws ApplicationException {
        Transfer existTransfer = transferRepository.findByIdWithPlayers(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TRANSFER_NOT_FOUND));

        return mapper.getTransferDtoFromEntity(existTransfer, true);
    }

    @Override
    public PagingDataRespDto<TransferDto> getAll(PagingDataReqDto pageRequest) throws ApplicationException {
        int pageSize = pageRequest.getPageSize();
        int pageNumber = pageRequest.getPageNumber();

        Page<Transfer> transferPage = null;
        try {
            transferPage = transferRepository.findAllWithPlayers(
                    PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "askPrice")
            );
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationError.FILTER_CRITERIA_ERROR, ex.getMessage());
        }

        return new PagingDataRespDto<>(
                transferPage.getTotalElements(),
                transferPage.getTotalPages(),
                transferPage.get().map(t -> mapper.getTransferDtoFromEntity(t, true))
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public List<TransferDto> getAll() {
        List<Transfer> existTransfers = transferRepository.findAllWithPlayers();
        return existTransfers.stream().map(t -> mapper.getTransferDtoFromEntity(t, true)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransferDto update(long id, TransferUpdateDto transfer, Long teamId) throws ApplicationException {
        Transfer existTransfer = transferRepository.findByIdWithPlayers(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TRANSFER_NOT_FOUND));

        if (teamId != null && existTransfer.getPlayer().getTeam().getId() != teamId) {
            throw new ApplicationException(ApplicationError.UNAUTHORIZED_USER_ERROR);
        }

        existTransfer.setAskPrice(transfer.getAskPrice());

        Transfer savedTransfer = transferRepository.save(existTransfer);
        return mapper.getTransferDtoFromEntity(savedTransfer, true);
    }

    @Override
    @Transactional
    public boolean delete(long id, Long teamId) throws ApplicationException {
        Transfer existTransfer = transferRepository.findByIdWithPlayers(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.TRANSFER_NOT_FOUND));

        Player transferredPlayer = existTransfer.getPlayer();
        if (teamId != null && existTransfer.getPlayer().getTeam().getId() != teamId) {
            Team toTeam = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ApplicationException(ApplicationError.TEAM_NOT_FOUND));

            double toTeamBudget = toTeam.getBudget();
            if (toTeamBudget < existTransfer.getAskPrice()) {
                throw new ApplicationException(ApplicationError.TEAM_INSUFFICIENT_BUDGET);
            }
            // to team budget decreases
            toTeam.setBudget(toTeamBudget - existTransfer.getAskPrice());

            // from team budget increases
            Team fromTeam = transferredPlayer.getTeam();
            double fromTeamBudget = fromTeam.getBudget();
            fromTeam.setBudget(fromTeamBudget + existTransfer.getAskPrice());

            transferredPlayer.setMarketValue(creator.createMarketValue(transferredPlayer.getMarketValue()));
            transferredPlayer.setTeam(toTeam);

            //toTeam.addPlayer(transferredPlayer);
            //fromTeam.removePlayer(transferredPlayer);

            teamRepository.save(fromTeam);
            teamRepository.save(toTeam);
            playerRepository.save(transferredPlayer);
        }
        transferredPlayer.setTransfer(null);
        transferRepository.delete(existTransfer);

        //return mapper.getPlayerDtoFromEntity(transferredPlayer, true, true);
        return true;
    }
}
