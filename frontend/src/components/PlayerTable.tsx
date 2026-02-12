import React from 'react';
import { Player } from '../types';
import { CurrencyDisplay } from './CurrencyDisplay';
import { Button } from './Button';
import { colors, typography, spacing, borderRadius, shadows } from '../styles/theme';

interface PlayerTableProps {
  players: Player[];
  playerStatuses: Record<string, 'ACTIVE' | 'ON_TRANSFER_LIST'>;
  editingPlayer: string | null;
  playerForm: Partial<Player>;
  loading: boolean;
  removingPlayer: string | null;
  onPlayerEdit: (player: Player) => void;
  onPlayerSave: (playerId: string) => void;
  onPlayerCancel: () => void;
  onPutOnTransferList: (player: Player) => void;
  onEditTransferPrice: (player: Player) => void;
  onRemoveFromTransferList: (player: Player) => void;
  onPlayerFormChange: (form: Partial<Player>) => void;
}

export const PlayerTable: React.FC<PlayerTableProps> = ({
  players,
  playerStatuses,
  editingPlayer,
  playerForm,
  loading,
  removingPlayer,
  onPlayerEdit,
  onPlayerSave,
  onPlayerCancel,
  onPutOnTransferList,
  onEditTransferPrice,
  onRemoveFromTransferList,
  onPlayerFormChange
}) => {
  if (players.length === 0) {
    return (
      <div style={{
        padding: spacing.xl,
        textAlign: 'center',
        color: colors.text.secondary
      }}>
        No players found
      </div>
    );
  }

  return (
    <>
      <div style={{
        border: `1px solid ${colors.border}`,
        borderRadius: borderRadius.lg,
        boxShadow: shadows.card,
        backgroundColor: colors.surface,
        overflow: 'hidden'
      }}>
        <table style={{
          width: '100%',
          borderCollapse: 'collapse',
          backgroundColor: colors.surface,
          tableLayout: 'fixed'
        }}>
          <thead>
            <tr style={{
              background: colors.gradients.card,
              borderBottom: `2px solid ${colors.border}`
            }}>
              <th style={{
                padding: spacing.md,
                textAlign: 'left',
                fontSize: typography.sm,
                fontWeight: '600',
                color: colors.text.primary,
                borderRight: `1px solid ${colors.border}`,
                width: '20%'
              }}>
                Name
              </th>
              <th style={{
                padding: spacing.md,
                textAlign: 'left',
                fontSize: typography.sm,
                fontWeight: '600',
                color: colors.text.primary,
                borderRight: `1px solid ${colors.border}`,
                width: '12%'
              }}>
                Position
              </th>
              <th style={{
                padding: spacing.md,
                textAlign: 'left',
                fontSize: typography.sm,
                fontWeight: '600',
                color: colors.text.primary,
                borderRight: `1px solid ${colors.border}`,
                width: '8%'
              }}>
                Age
              </th>
              <th style={{
                padding: spacing.md,
                textAlign: 'left',
                fontSize: typography.sm,
                fontWeight: '600',
                color: colors.text.primary,
                borderRight: `1px solid ${colors.border}`,
                width: '15%'
              }}>
                Country
              </th>
              <th style={{
                padding: spacing.md,
                textAlign: 'left',
                fontSize: typography.sm,
                fontWeight: '600',
                color: colors.text.primary,
                borderRight: `1px solid ${colors.border}`,
                width: '15%'
              }}>
                Market Value
              </th>
              <th style={{
                padding: spacing.md,
                textAlign: 'center',
                fontSize: typography.sm,
                fontWeight: '600',
                color: colors.text.primary,
                borderRight: `1px solid ${colors.border}`,
                width: '10%'
              }}>
                Status
              </th>
              <th style={{
                padding: spacing.md,
                textAlign: 'center',
                fontSize: typography.sm,
                fontWeight: '600',
                color: colors.text.primary,
                width: '20%'
              }}>
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {players.map((player, index) => (
              <tr key={player.id} style={{
                backgroundColor: index % 2 === 0 ? colors.surface : colors.background,
                borderBottom: `1px solid ${colors.border}`,
                transition: 'all 0.2s ease'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = colors.background;
                e.currentTarget.style.transform = 'scale(1.001)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = index % 2 === 0 ? colors.surface : colors.background;
                e.currentTarget.style.transform = 'scale(1)';
              }}>
                <td style={{
                  padding: spacing.md,
                  fontSize: typography.sm,
                  color: colors.text.primary,
                  borderRight: `1px solid ${colors.border}`,
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap'
                }}>
                  {editingPlayer === player.id ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: spacing.xs }}>
                      <input
                        type="text"
                        value={playerForm.firstName || ''}
                        onChange={(e) => onPlayerFormChange({ ...playerForm, firstName: e.target.value })}
                        placeholder="First name"
                        style={{
                          width: '100%',
                          padding: '4px',
                          border: `1px solid ${colors.border}`,
                          borderRadius: borderRadius.sm,
                          fontSize: typography.sm
                        }}
                      />
                      <input
                        type="text"
                        value={playerForm.lastName || ''}
                        onChange={(e) => onPlayerFormChange({ ...playerForm, lastName: e.target.value })}
                        placeholder="Last name"
                        style={{
                          width: '100%',
                          padding: '4px',
                          border: `1px solid ${colors.border}`,
                          borderRadius: borderRadius.sm,
                          fontSize: typography.sm
                        }}
                      />
                    </div>
                  ) : (
                    <div style={{ fontWeight: '500' }} title={`${player.firstName} ${player.lastName}`}>
                      {player.firstName} {player.lastName}
                    </div>
                  )}
                </td>
                <td style={{
                  padding: spacing.md,
                  fontSize: typography.sm,
                  color: colors.text.secondary,
                  borderRight: `1px solid ${colors.border}`
                }}>
                  {editingPlayer === player.id ? (
                    <select
                      value={playerForm.position || ''}
                      onChange={(e) => onPlayerFormChange({ ...playerForm, position: e.target.value as Player['position'] })}
                      style={{
                        width: '100%',
                        padding: '4px',
                        border: `1px solid ${colors.border}`,
                        borderRadius: borderRadius.sm,
                        fontSize: typography.sm
                      }}
                    >
                      <option value="GOALKEEPER">Goalkeeper</option>
                      <option value="DEFENDER">Defender</option>
                      <option value="MIDFIELDER">Midfielder</option>
                      <option value="ATTACKER">Attacker</option>
                    </select>
                  ) : (
                    player.position
                  )}
                </td>
                <td style={{
                  padding: spacing.md,
                  fontSize: typography.sm,
                  color: colors.text.secondary,
                  borderRight: `1px solid ${colors.border}`
                }}>
                  {editingPlayer === player.id ? (
                    <input
                      type="number"
                      value={playerForm.age || ''}
                      onChange={(e) => onPlayerFormChange({ ...playerForm, age: Number(e.target.value) })}
                      style={{
                        width: '60px',
                        padding: '4px',
                        border: `1px solid ${colors.border}`,
                        borderRadius: borderRadius.sm,
                        fontSize: typography.sm
                      }}
                    />
                  ) : (
                    `${player.age} years`
                  )}
                </td>
                <td style={{
                  padding: spacing.md,
                  fontSize: typography.sm,
                  color: colors.text.secondary,
                  borderRight: `1px solid ${colors.border}`,
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap'
                }}>
                  {editingPlayer === player.id ? (
                    <input
                      type="text"
                      value={playerForm.country || ''}
                      onChange={(e) => onPlayerFormChange({ ...playerForm, country: e.target.value })}
                      style={{
                        width: '100%',
                        padding: '4px',
                        border: `1px solid ${colors.border}`,
                        borderRadius: borderRadius.sm,
                        fontSize: typography.sm
                      }}
                    />
                  ) : (
                    <span title={player.country}>
                      {player.country}
                    </span>
                  )}
                </td>
                <td style={{
                  padding: spacing.md,
                  fontSize: typography.sm,
                  color: colors.text.primary,
                  borderRight: `1px solid ${colors.border}`
                }}>
                  <CurrencyDisplay amount={player.marketValue} />
                </td>
                <td style={{
                  padding: spacing.md,
                  fontSize: typography.sm,
                  textAlign: 'center'
                }}>
                  {(() => {
                    const playerStatus = playerStatuses[player.id] || 'ACTIVE';
                    return (
                      <span style={{
                        padding: '2px 8px',
                        fontSize: '12px',
                        fontWeight: '500',
                        borderRadius: '9999px',
                        backgroundColor: playerStatus === 'ACTIVE' ? '#dcfce7' : 
                                       playerStatus === 'ON_TRANSFER_LIST' ? '#fef3c7' : '#e5e7eb',
                        color: playerStatus === 'ACTIVE' ? '#166534' : 
                                playerStatus === 'ON_TRANSFER_LIST' ? '#92400e' : '#6b7280'
                      }}>
                        {playerStatus === 'ACTIVE' ? 'Active' : 
                         playerStatus === 'ON_TRANSFER_LIST' ? 'On Transfer List' : 'Unknown'}
                      </span>
                    );
                  })()}
                </td>
                <td style={{
                  padding: spacing.md,
                  fontSize: typography.sm,
                  textAlign: 'center'
                }}>
                  {(() => {
                    const playerStatus = playerStatuses[player.id] || 'ACTIVE';
                    if (editingPlayer === player.id) {
                      return (
                        <div style={{ 
                          display: 'flex', 
                          gap: '4px', 
                          justifyContent: 'center',
                          flexWrap: 'wrap'
                        }}>
                          <Button
                            onClick={() => onPlayerSave(player.id)}
                            disabled={loading}
                            variant="success"
                            size="sm"
                            loading={loading}
                          >
                            Save
                          </Button>
                          <Button
                            onClick={onPlayerCancel}
                            variant="secondary"
                            size="sm"
                          >
                            Cancel
                          </Button>
                        </div>
                      );
                    } else if (playerStatus === 'ACTIVE') {
                      return (
                        <div style={{ 
                          display: 'flex', 
                          gap: '4px', 
                          justifyContent: 'center',
                          flexWrap: 'wrap'
                        }}>
                          <Button
                            onClick={() => onPlayerEdit(player)}
                            variant="edit"
                            size="sm"
                          >
                            Edit
                          </Button>
                          <Button
                            onClick={() => onPutOnTransferList(player)}
                            variant="transfer"
                            size="sm"
                          >
                            Transfer
                          </Button>
                        </div>
                      );
                    } else {
                      return (
                        <div style={{ 
                          display: 'flex', 
                          gap: '4px', 
                          justifyContent: 'center',
                          flexWrap: 'wrap'
                        }}>
                          <Button
                            onClick={() => onPlayerEdit(player)}
                            variant="edit"
                            size="sm"
                          >
                            Edit
                          </Button>
                          <Button
                            onClick={() => onEditTransferPrice(player)}
                            variant="edit"
                            size="sm"
                          >
                            Price
                          </Button>
                          <Button
                            onClick={() => onRemoveFromTransferList(player)}
                            disabled={removingPlayer === player.id}
                            variant="danger"
                            size="sm"
                            loading={removingPlayer === player.id}
                          >
                            {removingPlayer === player.id ? "..." : "Remove"}
                          </Button>
                        </div>
                      );
                    }
                  })()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      <div style={{
        padding: spacing.md,
        background: colors.gradients.card,
        borderTop: `2px solid ${colors.border}`,
        borderBottom: `1px solid ${colors.border}`,
        borderRight: `1px solid ${colors.border}`,
        borderLeft: `1px solid ${colors.border}`,
        borderRadius: `0 0 ${borderRadius.lg} ${borderRadius.lg}`,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        boxShadow: 'inset 0 1px 0 rgba(0,0,0,0.05)'
      }}>
        <span style={{
          fontSize: typography.sm,
          fontWeight: '600',
          color: colors.text.primary
        }}>
          Total Players
        </span>
        <span style={{
          fontSize: typography.sm,
          fontWeight: '600',
          color: colors.primary
        }}>
          {players.length}
        </span>
      </div>
    </>
  );
};
