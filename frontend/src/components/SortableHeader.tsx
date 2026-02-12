import React from 'react';
import { colors, typography, spacing, borderRadius } from '../styles/theme';

interface SortableHeaderProps {
  children: React.ReactNode;
  sortKey: string;
  label: string;
  currentSortKey?: string;
  sortDirection?: 'asc' | 'desc';
  onSort: (key: string) => void;
}

export const SortableHeader: React.FC<SortableHeaderProps> = ({
  children,
  sortKey,
  label,
  currentSortKey,
  sortDirection,
  onSort
}) => (
  <button
    onClick={() => onSort(sortKey)}
    style={{
      background: 'none',
      border: 'none',
      color: colors.text.primary,
      fontSize: typography.sm,
      fontWeight: '500',
      cursor: 'pointer',
      padding: spacing.xs,
      borderRadius: borderRadius.sm,
      display: 'flex',
      alignItems: 'center',
      gap: spacing.xs,
      transition: 'background-color 0.2s'
    }}
    onMouseEnter={(e) => {
      e.currentTarget.style.backgroundColor = colors.border;
    }}
    onMouseLeave={(e) => {
      e.currentTarget.style.backgroundColor = 'transparent';
    }}
  >
    {label}
    {currentSortKey === sortKey && (
      <span style={{ fontSize: '10px' }}>
        {sortDirection === 'asc' ? ' ↑' : ' ↓'}
      </span>
    )}
  </button>
);
