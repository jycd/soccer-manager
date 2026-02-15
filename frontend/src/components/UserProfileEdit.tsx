import React, { useState, useEffect, useCallback } from 'react';
import { userAPI, User, UserUpdateRequest } from '../services/api';
import { Button, Modal } from '../components';
import { colors, typography, spacing, borderRadius } from '../styles/theme';

interface UserProfileEditProps {
  userId: string;
  isOpen: boolean;
  onClose: () => void;
  onUpdate: (user: User) => void;
  onDelete?: () => void;
}

export const UserProfileEdit: React.FC<UserProfileEditProps> = ({
  userId,
  isOpen,
  onClose,
  onUpdate,
  onDelete
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState<UserUpdateRequest>({});
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const fetchUser = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const userData = await userAPI.getCurrentUser(userId);
      setUser(userData);
      setFormData({
        email: userData.email,
        fullName: userData.fullName,
      });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load user data');
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    if (isOpen && userId) {
      fetchUser();
    } else if (isOpen && !userId) {
      setError('User ID not found. Please try logging out and logging back in.');
    }
  }, [isOpen, userId, fetchUser]);

  const handleSubmit = useCallback(async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const updatedUser = await userAPI.updateUser(userId, formData);
      onUpdate(updatedUser);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update user');
    } finally {
      setLoading(false);
    }
  }, [userId, formData, onUpdate, onClose]);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value || undefined
    }));
  }, []);

  const handleDeleteAccount = useCallback(async () => {
    if (!userId) return;
    
    setDeleting(true);
    setError('');
    
    try {
      await userAPI.deleteUser(userId);
      setDeleteModalOpen(false);
      if (onDelete) {
        onDelete();
      }
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete account');
    } finally {
      setDeleting(false);
    }
  }, [userId, onDelete, onClose]);

  const handleDeleteClick = useCallback(() => {
    setDeleteModalOpen(true);
  }, []);

  const handleCancelDelete = useCallback(() => {
    setDeleteModalOpen(false);
  }, []);

  if (!isOpen) return null;

  return (
    <>
      <Modal isOpen={isOpen} onClose={onClose} title="Edit Profile">
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {loading && !user && (
            <div style={{ textAlign: 'center', padding: '20px' }}>
              <div style={{ color: '#6b7280' }}>Loading...</div>
            </div>
          )}

          {error && (
            <div style={{ 
              backgroundColor: '#fee2e2', 
              border: '1px solid #fecaca', 
              color: '#dc2626', 
              padding: '12px', 
              borderRadius: '6px' 
            }}>
              {error}
            </div>
          )}

          {!loading && user && (
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '14px', fontWeight: '500', marginBottom: '6px', color: '#374151' }}>
                  Email
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email || ''}
                  onChange={handleChange}
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontSize: '14px'
                  }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '14px', fontWeight: '500', marginBottom: '6px', color: '#374151' }}>
                  Full Name
                </label>
                <input
                  type="text"
                  name="fullName"
                  value={formData.fullName || ''}
                  onChange={handleChange}
                  maxLength={60}
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontSize: '14px'
                  }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '14px', fontWeight: '500', marginBottom: '6px', color: '#374151' }}>
                  New Password (leave blank to keep current)
                </label>
                <input
                  type="password"
                  name="password"
                  value={formData.password || ''}
                  onChange={handleChange}
                  placeholder="Enter new password"
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontSize: '14px'
                  }}
                />
              </div>

              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '8px' }}>
                <Button
                  variant="secondary"
                  onClick={onClose}
                  disabled={loading}
                >
                  Cancel
                </Button>
                <Button
                  variant="primary"
                  type="submit"
                  disabled={loading}
                >
                  {loading ? 'Saving...' : 'Save Changes'}
                </Button>
              </div>
            </form>
          )}

          {/* Delete Account Section */}
          {!loading && user && (
            <div style={{
              borderTop: `1px solid ${colors.border}`,
              paddingTop: spacing.lg,
              marginTop: spacing.lg
            }}>
              <div style={{
                marginBottom: spacing.md
              }}>
                <h4 style={{
                  fontSize: typography.base,
                  fontWeight: '600',
                  color: colors.text.primary,
                  marginBottom: spacing.sm
                }}>
                  Danger Zone
                </h4>
                <p style={{
                  fontSize: typography.sm,
                  color: colors.text.secondary,
                  margin: 0
                }}>
                  Once you delete your account, there is no going back. Please be certain.
                </p>
              </div>
              <Button
                variant="danger"
                size="md"
                onClick={handleDeleteClick}
                disabled={loading || deleting}
              >
                Delete Account
              </Button>
            </div>
          )}
        </div>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={deleteModalOpen}
        onClose={handleCancelDelete}
        title="Delete Account"
        width="400px"
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: spacing.lg }}>
          <div style={{
            fontSize: typography.base,
            color: colors.text.secondary,
            textAlign: 'center',
            padding: spacing.md,
            backgroundColor: '#fef2f2',
            borderRadius: borderRadius.md,
            border: '1px solid #fecaca'
          }}>
            <div style={{
              fontSize: typography.lg,
              fontWeight: '600',
              color: colors.error,
              marginBottom: spacing.sm
            }}>
              ⚠️ This action cannot be undone
            </div>
            <p style={{ margin: 0 }}>
              Are you sure you want to delete your account? This will permanently remove:
            </p>
            <ul style={{
              textAlign: 'left',
              margin: `${spacing.sm} 0`,
              paddingLeft: spacing.lg
            }}>
              <li>Your user profile and all personal data</li>
              <li>Your team and all team information</li>
              <li>All players on your team</li>
              <li>Any transfer listings you created</li>
            </ul>
            <p style={{ margin: 0, fontWeight: '500' }}>
              You will need to create a new account to use the application again.
            </p>
          </div>

          <div style={{
            display: 'flex',
            gap: spacing.md,
            justifyContent: 'flex-end'
          }}>
            <Button
              onClick={handleCancelDelete}
              variant="secondary"
              size="md"
              disabled={deleting}
            >
              Cancel
            </Button>
            <Button
              onClick={handleDeleteAccount}
              variant="danger"
              size="md"
              loading={deleting}
              disabled={deleting}
            >
              {deleting ? 'Deleting...' : 'Delete Account'}
            </Button>
          </div>
        </div>
      </Modal>
    </>
  );
};
