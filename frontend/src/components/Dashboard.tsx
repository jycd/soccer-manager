import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { Team, Transfer, Player } from '../types';
import { transferAPI, teamAPI, playerAPI } from '../services/api';
import { TeamStats } from './TeamStats';
import { PlayerTable } from './PlayerTable';
import { Modal } from './Modal';
import { Button } from './Button';
import { CurrencyDisplay } from './CurrencyDisplay';
import { colors, typography, spacing, borderRadius } from '../styles/theme';

interface SortConfig {
  key: keyof Team['players'][0];
  direction: 'asc' | 'desc';
}

interface DashboardProps {
  team: Team;
  onTeamUpdate?: (team: Team) => void;
}

const POSITION_ORDER: Record<string, number> = {
  'GOALKEEPER': 0,
  'DEFENDER': 1,
  'MIDFIELDER': 2,
  'ATTACKER': 3,
  'goalkeeper': 0,
  'defender': 1,
  'midfielder': 2,
  'attacker': 3
} as const;

export const Dashboard: React.FC<DashboardProps> = ({ team, onTeamUpdate }) => {
  const [sortConfig, setSortConfig] = useState<SortConfig>({
    key: 'position',
    direction: 'asc'
  });
  const [transfers, setTransfers] = useState<Transfer[]>([]);
  const [transferingPlayer, setTransferingPlayer] = useState<string | null>(null);
  const [showTransferModal, setShowTransferModal] = useState(false);
  const [selectedPlayer, setSelectedPlayer] = useState<Player | null>(null);
  const [askPrice, setAskPrice] = useState('');
  const [error, setError] = useState('');
  const [isEditMode, setIsEditMode] = useState(false);
  const [currentTransferId, setCurrentTransferId] = useState<string | null>(null);
  const [showRemoveConfirm, setShowRemoveConfirm] = useState(false);
  const [playerToRemove, setPlayerToRemove] = useState<Player | null>(null);
  const [removingPlayer, setRemovingPlayer] = useState<string | null>(null);
  
  // Team editing state
  const [editingTeam, setEditingTeam] = useState(false);
  const [teamForm, setTeamForm] = useState({ name: team.name, country: team.country });
  const [loading, setLoading] = useState(false);
  
  // Player editing state
  const [editingPlayer, setEditingPlayer] = useState<string | null>(null);
  const [playerForm, setPlayerForm] = useState<Partial<Player>>({});

  useEffect(() => {
    const fetchTransfers = async () => {
      try {
        const transfersData = await transferAPI.getTransfers();
        setTransfers(transfersData);
      } catch (err) {
        console.error('Failed to fetch transfers:', err);
      }
    };
    fetchTransfers();
  }, []);

  const getPlayerStatus = useCallback((playerId: string): 'ACTIVE' | 'ON_TRANSFER_LIST' => {
    const isOnTransferList = transfers.some(transfer => transfer.player.id === playerId);
    return isOnTransferList ? 'ON_TRANSFER_LIST' : 'ACTIVE';
  }, [transfers]);

  const playerStatuses = useMemo(() => {
    const statuses: Record<string, 'ACTIVE' | 'ON_TRANSFER_LIST'> = {};
    team.players?.forEach(player => {
      statuses[player.id] = getPlayerStatus(player.id);
    });
    return statuses;
  }, [team.players, getPlayerStatus]);

  const sortedPlayers = useMemo(() => {
    if (!team.players) return [];
    
    const sorted = [...team.players].sort((a, b) => {
      // Special handling for position sorting
      if (sortConfig.key === 'position') {
        const aPositionOrder = POSITION_ORDER[a.position] ?? POSITION_ORDER[a.position?.toLowerCase()] ?? 999;
        const bPositionOrder = POSITION_ORDER[b.position] ?? POSITION_ORDER[b.position?.toLowerCase()] ?? 999;
        
        return sortConfig.direction === 'asc' 
          ? aPositionOrder - bPositionOrder
          : bPositionOrder - aPositionOrder;
      }
      
      // Regular sorting for other fields
      const aValue = a[sortConfig.key] ?? '';
      const bValue = b[sortConfig.key] ?? '';
      
      if (aValue < bValue) {
        return sortConfig.direction === 'asc' ? -1 : 1;
      }
      if (aValue > bValue) {
        return sortConfig.direction === 'asc' ? 1 : -1;
      }
      return 0;
    });
    
    return sorted;
  }, [team.players, sortConfig]);

  const handleSort = useCallback((key: string) => {
    setSortConfig(current => ({
      key: key as keyof Team['players'][0],
      direction: current.key === key && current.direction === 'asc' ? 'desc' : 'asc'
    }));
  }, []);

  const handlePutOnTransferList = useCallback((player: Player) => {
    setSelectedPlayer(player);
    setAskPrice(player.marketValue.toString());
    setShowTransferModal(true);
    setError('');
    setIsEditMode(false);
    setCurrentTransferId(null);
  }, []);

  const handleEditTransferPrice = useCallback((player: Player) => {
    const transfer = transfers.find(t => t.player.id === player.id);
    if (transfer) {
      setSelectedPlayer(player);
      setAskPrice(transfer.askPrice);
      setShowTransferModal(true);
      setError('');
      setIsEditMode(true);
      setCurrentTransferId(transfer.id);
    }
  }, [transfers]);

  const handleRemoveFromTransferList = useCallback((player: Player) => {
    setPlayerToRemove(player);
    setShowRemoveConfirm(true);
  }, []);

  const confirmRemoveTransfer = async () => {
    if (!playerToRemove) return;

    setRemovingPlayer(playerToRemove.id);
    
    try {
      await transferAPI.removeTransfer(team.id, playerToRemove.id);
      const transfersData = await transferAPI.getTransfers();
      setTransfers(transfersData);
      setShowRemoveConfirm(false);
      setPlayerToRemove(null);
    } catch (err) {
      setError('Failed to remove player from transfer list');
    } finally {
      setRemovingPlayer(null);
    }
  };

  const cancelRemoveTransfer = () => {
    setShowRemoveConfirm(false);
    setPlayerToRemove(null);
  };

  const handleCreateTransfer = async () => {
    if (!selectedPlayer || !askPrice) {
      setError('Please enter a valid ask price');
      return;
    }

    setTransferingPlayer(selectedPlayer.id);
    setError('');
    
    try {
      if (isEditMode && currentTransferId) {
        await transferAPI.updateTransfer(team.id, currentTransferId, parseFloat(askPrice));
      } else {
        await transferAPI.createTransfer(team.id, selectedPlayer.id, parseFloat(askPrice));
      }
      const transfersData = await transferAPI.getTransfers();
      setTransfers(transfersData);
      setShowTransferModal(false);
      setSelectedPlayer(null);
      setAskPrice('');
      setIsEditMode(false);
      setCurrentTransferId(null);
    } catch (err) {
      setError(isEditMode ? 'Failed to update transfer price' : 'Failed to put player on transfer list');
    } finally {
      setTransferingPlayer(null);
    }
  };

  const handleCancelTransfer = useCallback(() => {
    setShowTransferModal(false);
    setSelectedPlayer(null);
    setAskPrice('');
    setError('');
    setIsEditMode(false);
    setCurrentTransferId(null);
  }, []);

  // Team editing functions
  const handleTeamEdit = useCallback(() => {
    setEditingTeam(true);
    setTeamForm({ name: team.name, country: team.country });
  }, [team.name, team.country]);

  const handleTeamSave = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      await teamAPI.updateTeam(team.id, teamForm);
      // Refetch full team data to get updated players
      if (onTeamUpdate) {
        const updatedTeam = await teamAPI.getMyTeam(team.id);
        onTeamUpdate(updatedTeam);
      }
      setEditingTeam(false);
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to update team");
    } finally {
      setLoading(false);
    }
  }, [team.id, teamForm, onTeamUpdate]);

  const handleTeamCancel = useCallback(() => {
    setEditingTeam(false);
    setTeamForm({ name: team.name, country: team.country });
  }, [team.name, team.country]);

  const handleTeamFormChange = useCallback((form: { name: string; country: string }) => {
    setTeamForm(form);
  }, []);

  // Player editing functions
  const handlePlayerEdit = useCallback((player: Player) => {
    setEditingPlayer(player.id);
    setPlayerForm({
      firstName: player.firstName,
      lastName: player.lastName,
      age: player.age,
      country: player.country,
      position: player.position,
    });
  }, []);

  const handlePlayerSave = useCallback(async (playerId: string) => {
    setLoading(true);
    setError('');
    try {
      await playerAPI.updatePlayer(team.id, playerId, playerForm);
      // Refetch team to get updated player data
      if (onTeamUpdate) {
        const updatedTeam = await teamAPI.getMyTeam(team.id);
        onTeamUpdate(updatedTeam);
      }
      setEditingPlayer(null);
      setPlayerForm({});
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to update player");
    } finally {
      setLoading(false);
    }
  }, [team.id, playerForm, onTeamUpdate]);

  const handlePlayerCancel = useCallback(() => {
    setEditingPlayer(null);
    setPlayerForm({});
  }, []);

  const handlePlayerFormChange = useCallback((form: Partial<Player>) => {
    setPlayerForm(form);
  }, []);

  return (
    <div style={{ 
      display: 'flex', 
      flexDirection: 'column', 
      gap: spacing.lg,
      backgroundColor: colors.background,
      minHeight: '100vh',
      padding: spacing.lg
    }}>
      <TeamStats
        team={team}
        editingTeam={editingTeam}
        teamForm={teamForm}
        loading={loading}
        onTeamEdit={handleTeamEdit}
        onTeamSave={handleTeamSave}
        onTeamCancel={handleTeamCancel}
        onTeamFormChange={handleTeamFormChange}
      />

      <div style={{
        backgroundColor: colors.surface,
        border: `1px solid ${colors.border}`,
        borderRadius: borderRadius.lg,
        boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
        overflow: 'hidden'
      }}>
        <div style={{
          padding: spacing.lg,
          borderBottom: `1px solid ${colors.border}`
        }}>
          <h3 style={{
            fontSize: typography.lg,
            fontWeight: '600',
            color: colors.text.primary,
            marginBottom: spacing.xs
          }}>
            Players
          </h3>
          <p style={{
            fontSize: typography.sm,
            color: colors.text.secondary
          }}>
            View your team players and their details.
          </p>
        </div>
        
        <PlayerTable
          players={sortedPlayers}
          playerStatuses={playerStatuses}
          editingPlayer={editingPlayer}
          playerForm={playerForm}
          loading={loading}
          removingPlayer={removingPlayer}
          onPlayerEdit={handlePlayerEdit}
          onPlayerSave={handlePlayerSave}
          onPlayerCancel={handlePlayerCancel}
          onPutOnTransferList={handlePutOnTransferList}
          onEditTransferPrice={handleEditTransferPrice}
          onRemoveFromTransferList={handleRemoveFromTransferList}
          onPlayerFormChange={handlePlayerFormChange}
        />
      </div>

      <Modal
        isOpen={showTransferModal}
        title={isEditMode ? 'Edit Transfer Price' : 'Put Player on Transfer List'}
      >
        <div style={{ marginBottom: spacing.md }}>
          <p style={{
            fontSize: typography.sm,
            color: colors.text.secondary,
            marginBottom: spacing.sm
          }}>
            Player: <strong>{selectedPlayer?.firstName} {selectedPlayer?.lastName}</strong>
          </p>
          <p style={{
            fontSize: typography.sm,
            color: colors.text.secondary,
            marginBottom: spacing.sm
          }}>
            Position: {selectedPlayer?.position} • Age: {selectedPlayer?.age} years
          </p>
          <p style={{
            fontSize: typography.sm,
            color: colors.text.secondary,
            marginBottom: spacing.sm
          }}>
            Current Market Value: {selectedPlayer && <CurrencyDisplay amount={selectedPlayer.marketValue} />}
          </p>
        </div>

        <div style={{ marginBottom: spacing.md }}>
          <label style={{
            display: 'block',
            fontSize: typography.sm,
            fontWeight: '500',
            color: colors.text.primary,
            marginBottom: spacing.xs
          }}>
            Ask Price ($):
          </label>
          <input
            type="number"
            value={askPrice}
            onChange={(e) => setAskPrice(e.target.value)}
            style={{
              width: '100%',
              padding: spacing.sm,
              border: `1px solid ${colors.border}`,
              borderRadius: borderRadius.sm,
              fontSize: typography.sm
            }}
            min="0"
            step="1000"
          />
        </div>

        {error && (
          <div style={{
            padding: spacing.sm,
            backgroundColor: '#fef2f2',
            border: '1px solid #fecaca',
            borderRadius: borderRadius.sm,
            marginBottom: spacing.md,
            fontSize: typography.sm,
            color: '#dc2626'
          }}>
            {error}
          </div>
        )}

        <div style={{
          display: 'flex',
          gap: spacing.sm,
          justifyContent: 'flex-end'
        }}>
          <Button
            onClick={handleCancelTransfer}
            disabled={transferingPlayer === selectedPlayer?.id}
            variant="secondary"
            size="md"
          >
            Cancel
          </Button>
          <Button
            onClick={handleCreateTransfer}
            disabled={transferingPlayer === selectedPlayer?.id || !askPrice}
            variant="warning"
            size="md"
            loading={transferingPlayer === selectedPlayer?.id}
          >
            {isEditMode ? 'Update Price' : 'Put on Transfer List'}
          </Button>
        </div>
      </Modal>

      <Modal
        isOpen={showRemoveConfirm}
        title="Remove from Transfer List"
      >
        <div style={{ marginBottom: spacing.md }}>
          <p style={{
            fontSize: typography.sm,
            color: colors.text.secondary,
            marginBottom: spacing.sm
          }}>
            Are you sure you want to remove <strong>{playerToRemove?.firstName} {playerToRemove?.lastName}</strong> from the transfer list?
          </p>
          <p style={{
            fontSize: typography.sm,
            color: colors.text.secondary,
            marginBottom: spacing.sm
          }}>
            Position: {playerToRemove?.position} • Age: {playerToRemove?.age} years
          </p>
          <p style={{
            fontSize: typography.sm,
            color: colors.text.secondary,
            marginBottom: spacing.sm
          }}>
            This player will no longer be available for other teams to buy.
          </p>
        </div>

        {error && (
          <div style={{
            padding: spacing.sm,
            backgroundColor: '#fef2f2',
            border: '1px solid #fecaca',
            borderRadius: borderRadius.sm,
            marginBottom: spacing.md,
            fontSize: typography.sm,
            color: '#dc2626'
          }}>
            {error}
          </div>
        )}

        <div style={{
          display: 'flex',
          gap: spacing.sm,
          justifyContent: 'flex-end'
        }}>
          <Button
            onClick={cancelRemoveTransfer}
            disabled={removingPlayer === playerToRemove?.id}
            variant="secondary"
            size="md"
          >
            Cancel
          </Button>
          <Button
            onClick={confirmRemoveTransfer}
            disabled={removingPlayer === playerToRemove?.id}
            variant="danger"
            size="md"
            loading={removingPlayer === playerToRemove?.id}
          >
            Remove
          </Button>
        </div>
      </Modal>
    </div>
  );
};
