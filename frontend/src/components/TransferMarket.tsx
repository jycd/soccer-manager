import React, { useState, useEffect, useCallback } from "react";
import { transferAPI } from "../services/api";
import { Transfer, Player } from "../types";
import { Button } from "./Button";
import { CurrencyDisplay } from "./CurrencyDisplay";
import { colors, typography, spacing, borderRadius, shadows } from "../styles/theme";

interface TransferMarketProps {
  teamId: string;
  teamPlayers?: Player[];
}

export const TransferMarket: React.FC<TransferMarketProps> = ({ teamId, teamPlayers = [] }) => {
  const [transfers, setTransfers] = useState<Transfer[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [buying, setBuying] = useState<string | null>(null);

  const isPlayerOnMyTeam = useCallback((playerId: string): boolean => {
    return teamPlayers.some(player => player.id === playerId);
  }, [teamPlayers]);

  useEffect(() => {
    fetchTransfers();
  }, []);

  const fetchTransfers = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const transfersData = await transferAPI.getTransfers();
      setTransfers(transfersData);
    } catch (err) {
      setError("Failed to load transfer market");
    } finally {
      setLoading(false);
    }
  }, []);

  const handleBuyPlayer = useCallback(async (transferId: string) => {
    setBuying(transferId);
    try {
      await transferAPI.buyPlayer(teamId, transferId);
      fetchTransfers();
    } catch (err) {
      setError("Failed to buy player");
    } finally {
      setBuying(null);
    }
  }, [teamId, fetchTransfers]);

  if (loading) {
    return (
      <div style={{ 
        padding: spacing.xl, 
        textAlign: "center", 
        color: colors.text.secondary,
        backgroundColor: colors.background,
        minHeight: '200px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        flexDirection: 'column',
        gap: spacing.md
      }}>
        <div className="spin" style={{
          width: '40px',
          height: '40px',
          border: `4px solid ${colors.border}`,
          borderTop: `4px solid ${colors.primary}`,
          borderRadius: '50%'
        }} />
        <span style={{ fontSize: typography.lg, fontWeight: '500' }}>
          Loading transfer market...
        </span>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ 
        padding: spacing.xl, 
        textAlign: "center", 
        color: colors.error,
        backgroundColor: '#fef2f2',
        border: `1px solid #fecaca`,
        borderRadius: borderRadius.lg,
        margin: spacing.lg
      }}>
        <div style={{ fontSize: typography.lg, fontWeight: '600', marginBottom: spacing.sm }}>
          ⚠️ Error
        </div>
        <div style={{ fontSize: typography.base }}>
          {error}
        </div>
      </div>
    );
  }

  return (
    <div style={{ 
      backgroundColor: colors.background,
      padding: spacing.lg,
      minHeight: '100vh'
    }}>
      {/* Header Section */}
      <div style={{
        background: colors.gradients.card,
        padding: spacing.xl,
        borderRadius: borderRadius.lg,
        boxShadow: shadows.lg,
        marginBottom: spacing.lg,
        position: 'relative',
        overflow: 'hidden'
      }}>
        {/* Accent border */}
        <div style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          height: '4px',
          background: colors.gradients.primary
        }} />
        
        <div style={{ display: 'flex', alignItems: 'center', gap: spacing.md, marginBottom: spacing.sm }}>
          <div style={{
            fontSize: typography.xl,
            color: colors.primary
          }}>
            ⚽
          </div>
          <div>
            <h3 style={{ 
              fontSize: typography['2xl'], 
              fontWeight: '700', 
              color: colors.text.primary, 
              marginBottom: spacing.xs,
              textTransform: 'uppercase',
              letterSpacing: '1px'
            }}>
              Transfer Market
            </h3>
            <p style={{ 
              fontSize: typography.base, 
              color: colors.text.secondary,
              margin: 0 
            }}>
              Browse and buy talented players from other teams to strengthen your squad
            </p>
          </div>
        </div>
        
        <div style={{
          display: 'flex',
          gap: spacing.lg,
          marginTop: spacing.md
        }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: spacing.sm,
            padding: spacing.sm + ' ' + spacing.md,
            backgroundColor: colors.success + '20',
            borderRadius: borderRadius.md,
            border: `1px solid ${colors.success}30`
          }}>
            <div style={{ color: colors.success, fontSize: typography.lg }}>📊</div>
            <div>
              <div style={{ fontSize: typography.sm, color: colors.text.secondary }}>Available Players</div>
              <div style={{ fontSize: typography.lg, fontWeight: '600', color: colors.success }}>
                {transfers.length}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Players List */}
      {transfers.length === 0 ? (
        <div style={{
          background: colors.surface,
          padding: spacing.xl,
          textAlign: "center", 
          color: colors.text.secondary,
          borderRadius: borderRadius.lg,
          boxShadow: shadows.card,
          border: `1px solid ${colors.border}`
        }}>
          <div style={{ fontSize: typography['2xl'], marginBottom: spacing.md, opacity: 0.5 }}>
            🔍
          </div>
          <div style={{ fontSize: typography.lg, fontWeight: '600', marginBottom: spacing.sm }}>
            No Players Available
          </div>
          <div style={{ fontSize: typography.base }}>
            Check back later for new transfer opportunities
          </div>
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gap: spacing.lg
        }}>
          {transfers.map((transfer, index) => (
            <div 
              key={transfer.id} 
              style={{
                background: colors.gradients.card,
                borderRadius: borderRadius.lg,
                boxShadow: shadows.card,
                overflow: 'hidden',
                transition: 'all 0.3s ease',
                border: `1px solid ${colors.border}`,
                position: 'relative'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-2px)';
                e.currentTarget.style.boxShadow = shadows.hover;
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = shadows.card;
              }}
            >
              {/* Status indicator */}
              <div style={{
                position: 'absolute',
                top: 0,
                left: 0,
                right: 0,
                height: '3px',
                background: transfer.status === 'COMPLETED' ? colors.success : colors.warning
              }} />
              
              <div style={{ padding: spacing.lg }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: spacing.lg }}>
                  {/* Player Info */}
                  <div style={{ flex: 1, display: 'flex', alignItems: 'center', gap: spacing.md }}>
                    <div style={{
                      width: '60px',
                      height: '60px',
                      borderRadius: borderRadius.full,
                      background: colors.gradients.primary,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: typography.xl,
                      color: 'white',
                      fontWeight: '700'
                    }}>
                      {transfer.player?.firstName?.charAt(0) || '?'}
                    </div>
                    <div>
                      <div style={{ 
                        fontSize: typography.lg, 
                        fontWeight: '700', 
                        color: colors.text.primary,
                        marginBottom: spacing.xs
                      }}>
                        {transfer.player?.firstName || ''} {transfer.player?.lastName || ''}
                      </div>
                      <div style={{ 
                        fontSize: typography.sm, 
                        color: colors.text.secondary,
                        marginBottom: spacing.xs
                      }}>
                        <span style={{
                          padding: '2px 8px',
                          backgroundColor: colors.primary + '20',
                          color: colors.primary,
                          borderRadius: borderRadius.full,
                          fontSize: '12px',
                          fontWeight: '600',
                          marginRight: spacing.xs
                        }}>
                          {transfer.player?.position || ''}
                        </span>
                        <span style={{ margin: `0 ${spacing.xs}` }}>•</span>
                        <span>{transfer.player?.age || 0} years</span>
                        <span style={{ margin: `0 ${spacing.xs}` }}>•</span>
                        <span>{transfer.player?.country || ''}</span>
                      </div>
                      <div style={{ 
                        fontSize: typography.sm, 
                        color: colors.text.muted,
                        display: 'flex',
                        alignItems: 'center',
                        gap: spacing.xs
                      }}>
                        <span>🏆</span>
                        From: {transfer.player?.team?.name || 'Unknown Team'}
                      </div>
                    </div>
                  </div>

                  {/* Price and Action */}
                  <div style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    gap: spacing.lg,
                    textAlign: 'right'
                  }}>
                    <div>
                      <div style={{ 
                        fontSize: typography.xl, 
                        fontWeight: '700', 
                        color: colors.success,
                        marginBottom: spacing.xs
                      }}>
                        <CurrencyDisplay amount={parseFloat(transfer.askPrice || '0')} />
                      </div>
                      <div style={{ 
                        fontSize: typography.sm, 
                        color: colors.text.secondary,
                        display: 'flex',
                        alignItems: 'center',
                        gap: spacing.xs,
                        justifyContent: 'flex-end'
                      }}>
                        <span style={{
                          padding: '2px 6px',
                          backgroundColor: transfer.status === 'COMPLETED' ? colors.success + '20' : colors.warning + '20',
                          color: transfer.status === 'COMPLETED' ? colors.success : colors.warning,
                          borderRadius: borderRadius.full,
                          fontSize: '11px',
                          fontWeight: '600'
                        }}>
                          {transfer.status || 'PENDING'}
                        </span>
                      </div>
                    </div>
                    
                    <div style={{ display: 'flex', gap: spacing.sm }}>
                      {(transfer.status === 'PENDING' || !transfer.status) && (
                        <>
                          {isPlayerOnMyTeam(transfer.player?.id || '') ? (
                            <div style={{
                              padding: spacing.sm + ' ' + spacing.md,
                              backgroundColor: colors.background,
                              color: colors.text.muted,
                              border: `1px solid ${colors.border}`,
                              borderRadius: borderRadius.md,
                              fontSize: typography.sm,
                              fontWeight: '600',
                              display: 'flex',
                              alignItems: 'center',
                              gap: spacing.xs
                            }}>
                              <span>⭐</span>
                              Your Player
                            </div>
                          ) : (
                            <Button
                              onClick={() => handleBuyPlayer(transfer.id)}
                              disabled={buying === transfer.id}
                              variant="success"
                              size="md"
                              loading={buying === transfer.id}
                            >
                              {buying === transfer.id ? "Buying..." : "Buy Player"}
                            </Button>
                          )}
                        </>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
