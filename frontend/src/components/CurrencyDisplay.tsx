import React from 'react';
import { colors, typography, spacing } from '../styles/theme';

interface CurrencyDisplayProps {
  amount: number | string;
  className?: string;
}

export const CurrencyDisplay: React.FC<CurrencyDisplayProps> = ({ amount, className }) => {
  const formattedAmount = Number(amount || 0).toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });

  return (
    <span className={className} style={{ color: colors.text.primary }}>
      ${formattedAmount}
    </span>
  );
};
