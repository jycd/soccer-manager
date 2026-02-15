import React from 'react';
import { colors, typography, spacing } from '../styles/theme';
import { formatCurrency } from '../utils';

interface CurrencyDisplayProps {
  amount: number | string;
  className?: string;
}

export const CurrencyDisplay: React.FC<CurrencyDisplayProps> = ({ amount, className }) => {
  const formattedAmount = formatCurrency(Number(amount || 0));

  return (
    <span className={className} style={{ color: colors.text.primary }}>
      {formattedAmount}
    </span>
  );
};
