import React, { useState, useEffect, useCallback } from 'react';
import { userAPI, User, UserUpdateRequest } from '../services/api';
import { Button } from './Button';
import { Modal } from './Modal';

interface UserProfileEditProps {
  userId: string;
  isOpen: boolean;
  onClose: () => void;
  onUpdate: (user: User) => void;
}

export const UserProfileEdit: React.FC<UserProfileEditProps> = ({
  userId,
  isOpen,
  onClose,
  onUpdate
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState<UserUpdateRequest>({});

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

  if (!isOpen) return null;

  return (
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
      </div>
    </Modal>
  );
};
