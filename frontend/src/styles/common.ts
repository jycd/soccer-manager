import { colors, spacing, typography, borderRadius, shadows } from '../styles/theme';

export const cardStyle = {
  backgroundColor: colors.surface,
  borderRadius: borderRadius.lg,
  boxShadow: shadows.md,
  overflow: 'hidden'
};

export const containerStyle = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: `0 ${spacing.lg}`
};

export const flexRow = {
  display: 'flex',
  alignItems: 'center',
  gap: spacing.md
};
