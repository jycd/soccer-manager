import React from 'react';
import { Button } from './Button';
import { CurrencyDisplay } from './CurrencyDisplay';
import { colors, typography, spacing, borderRadius, shadows } from '../styles/theme';

interface TeamStatsProps {
  team: {
    name: string;
    country: string;
    marketValue: string;
    budget: string;
  };
  editingTeam: boolean;
  teamForm: { name: string; country: string };
  loading: boolean;
  onTeamEdit: () => void;
  onTeamSave: () => void;
  onTeamCancel: () => void;
  onTeamFormChange: (form: { name: string; country: string }) => void;
}

export const TeamStats: React.FC<TeamStatsProps> = ({
  team,
  editingTeam,
  teamForm,
  loading,
  onTeamEdit,
  onTeamSave,
  onTeamCancel,
  onTeamFormChange
}) => {
  type StatItem = {
  label: string;
  value: string | number;
  format: 'text' | 'currency';
  editable: boolean;
  field?: 'name' | 'country';
};

const stats: StatItem[] = [
    {
      label: 'Team Name',
      value: team.name,
      format: 'text',
      editable: true,
      field: 'name'
    },
    {
      label: 'Country',
      value: team.country,
      format: 'text',
      editable: true,
      field: 'country'
    },
    {
      label: 'Team Value',
      value: parseFloat(team.marketValue || '0'),
      format: 'currency',
      editable: false
    },
    {
      label: 'Budget',
      value: parseFloat(team.budget || '0'),
      format: 'currency',
      editable: false
    }
  ];

  const handleFormChange = (field: 'name' | 'country', value: string) => {
    onTeamFormChange({
      ...teamForm,
      [field]: value
    });
  };

  return (
    <>
      {/* First row: Team Name and Country */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
        gap: spacing.lg,
        marginBottom: spacing.lg
      }}>
        {stats.filter(stat => stat.editable).map((stat, index) => (
          <div key={index} style={{
            padding: spacing.lg,
            background: colors.gradients.card,
            border: `1px solid ${colors.border}`,
            borderRadius: borderRadius.lg,
            boxShadow: shadows.card,
            transition: 'all 0.3s ease',
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
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div style={{ flex: 1 }}>
                <h3 style={{
                  fontSize: typography.sm,
                  fontWeight: '600',
                  color: colors.text.secondary,
                  marginBottom: spacing.sm,
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px'
                }}>
                  {stat.label}
                </h3>
                {editingTeam && stat.editable && stat.field ? (
                  <input
                    type="text"
                    value={teamForm[stat.field]}
                    onChange={(e) => handleFormChange(stat.field!, e.target.value)}
                    style={{
                      width: '100%',
                      padding: '12px',
                      border: `2px solid ${colors.border}`,
                      borderRadius: borderRadius.md,
                      fontSize: typography.lg,
                      fontWeight: '600',
                      color: colors.text.primary,
                      backgroundColor: colors.surface,
                      transition: 'border-color 0.2s ease',
                      outline: 'none'
                    }}
                    onFocus={(e) => {
                      e.target.style.borderColor = colors.primary;
                    }}
                    onBlur={(e) => {
                      e.target.style.borderColor = colors.border;
                    }}
                  />
                ) : (
                  <p style={{
                    fontSize: typography.xl,
                    fontWeight: '700',
                    color: colors.text.primary,
                    margin: 0
                  }}>
                    {stat.format === 'currency' ? (
                      <CurrencyDisplay amount={stat.value} />
                    ) : (
                      stat.value
                    )}
                  </p>
                )}
              </div>
              {stat.editable && (
                <div style={{ display: 'flex', gap: spacing.xs, marginLeft: spacing.md }}>
                  {editingTeam ? (
                    <>
                      <Button
                        onClick={onTeamSave}
                        disabled={loading}
                        variant="primary"
                        size="sm"
                        loading={loading}
                      >
                        Save
                      </Button>
                      <Button
                        onClick={onTeamCancel}
                        variant="secondary"
                        size="sm"
                      >
                        Cancel
                      </Button>
                    </>
                  ) : (
                    <Button
                      onClick={onTeamEdit}
                      variant="secondary"
                      size="sm"
                    >
                      Edit
                    </Button>
                  )}
                </div>
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Second row: Team Value and Budget */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
        gap: spacing.lg
      }}>
        {stats.filter(stat => !stat.editable).map((stat, index) => (
          <div key={index} style={{
            padding: spacing.lg,
            background: colors.gradients.card,
            border: `1px solid ${colors.border}`,
            borderRadius: borderRadius.lg,
            boxShadow: shadows.card,
            transition: 'all 0.3s ease',
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
              background: colors.gradients.success
            }} />
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div style={{ flex: 1 }}>
                <h3 style={{
                  fontSize: typography.sm,
                  fontWeight: '600',
                  color: colors.text.secondary,
                  marginBottom: spacing.sm,
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px'
                }}>
                  {stat.label}
                </h3>
                <p style={{
                  fontSize: typography.xl,
                  fontWeight: '700',
                  color: colors.success,
                  margin: 0
                }}>
                  {stat.format === 'currency' ? (
                    <CurrencyDisplay amount={stat.value} />
                  ) : (
                    stat.value
                  )}
                </p>
              </div>
              <div style={{
                display: 'flex',
                alignItems: 'center',
                color: colors.success,
                fontSize: typography.xl
              }}>
                💰
              </div>
            </div>
          </div>
        ))}
      </div>
    </>
  );
};
