import React from 'react';
import { colors, typography, spacing, borderRadius } from '../styles/theme';

interface ModalProps {
  isOpen: boolean;
  onClose?: () => void;
  title: string;
  children: React.ReactNode;
  width?: string;
}

export const Modal: React.FC<ModalProps> = ({
  isOpen,
  onClose,
  title,
  children,
  width = '400px'
}) => {
  if (!isOpen) return null;

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000
    }}>
      <div style={{
        backgroundColor: 'white',
        padding: spacing.lg,
        borderRadius: borderRadius.md,
        width,
        maxWidth: '90vw'
      }}>
        <h3 style={{
          fontSize: typography.lg,
          fontWeight: '600',
          color: colors.text.primary,
          marginBottom: spacing.md
        }}>
          {title}
        </h3>
        {children}
      </div>
    </div>
  );
};
