import React from 'react';
import { colors, typography, spacing, borderRadius } from '../styles/theme';

interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  disabled?: boolean;
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'edit' | 'transfer';
  size?: 'sm' | 'md';
  loading?: boolean;
  type?: 'button' | 'submit';
}

const variantStyles = {
  primary: {
    backgroundColor: colors.primary,
    hoverColor: '#5558e3'
  },
  secondary: {
    backgroundColor: colors.secondary,
    hoverColor: '#7c3aed'
  },
  success: {
    backgroundColor: colors.success,
    hoverColor: '#059669'
  },
  warning: {
    backgroundColor: colors.warning,
    hoverColor: '#d97706'
  },
  danger: {
    backgroundColor: colors.error,
    hoverColor: '#dc2626'
  },
  edit: {
    backgroundColor: colors.accent,
    hoverColor: '#0284c7'
  }
};

const sizeStyles = {
  sm: {
    padding: '4px 8px',
    fontSize: '11px'
  },
  md: {
    padding: `${spacing.sm} ${spacing.md}`,
    fontSize: typography.sm
  }
};

export const Button: React.FC<ButtonProps> = ({
  children,
  onClick,
  disabled = false,
  variant = 'primary',
  size = 'md',
  loading = false,
  type = 'button'
}) => {
  // Map transfer variant to warning for backward compatibility
  const actualVariant = variant === 'transfer' ? 'warning' : variant;
  const variantStyle = variantStyles[actualVariant];
  const sizeStyle = sizeStyles[size];

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      style={{
        padding: sizeStyle.padding,
        backgroundColor: disabled || loading ? '#9ca3af' : variantStyle.backgroundColor,
        color: 'white',
        border: 'none',
        borderRadius: size === 'sm' ? borderRadius.sm : borderRadius.md,
        fontSize: sizeStyle.fontSize,
        fontWeight: '600',
        cursor: disabled || loading ? 'not-allowed' : 'pointer',
        transition: 'all 0.2s ease',
        opacity: disabled || loading ? 0.7 : 1,
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        transform: 'translateY(0)'
      }}
      onMouseEnter={(e) => {
        if (!disabled && !loading) {
          e.currentTarget.style.backgroundColor = variantStyle.hoverColor;
          e.currentTarget.style.transform = 'translateY(-1px)';
          e.currentTarget.style.boxShadow = '0 4px 8px rgba(0,0,0,0.15)';
        }
      }}
      onMouseLeave={(e) => {
        if (!disabled && !loading) {
          e.currentTarget.style.backgroundColor = variantStyle.backgroundColor;
          e.currentTarget.style.transform = 'translateY(0)';
          e.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
        }
      }}
    >
      {loading ? 'Loading...' : children}
    </button>
  );
};
