import React, { useState, useEffect, useCallback } from 'react';
import { authAPI, teamAPI, playerAPI, userAPI, User } from './services/api';
import { Team, Player } from './types';
import { Dashboard, TransferMarket, UserProfileEdit, Button, Modal } from './components';
import { useAuth } from './hooks';
import { authStorage } from './utils/auth';
import { colors, spacing } from './styles/theme';
import { containerStyle } from './styles/common';

function App() {
  const { token, teamId, userId, setAuth, clearAuth } = useAuth();

  // Application state
  const [team, setTeam] = useState<Team | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<'dashboard' | 'transfers'>('dashboard');
  const [isUserProfileEditOpen, setIsUserProfileEditOpen] = useState(false);

  // Form state
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');

  useEffect(() => {
    const savedToken = authStorage.getToken();
    const savedTeamId = authStorage.getTeamId();
    const savedUserId = authStorage.getUserId();

    if (savedToken && savedTeamId && savedUserId) {
      setAuth(savedToken, savedTeamId, savedUserId);
    } else if (savedToken && !savedTeamId) {
      authStorage.clearAuth();
      clearAuth();
    }
  }, [setAuth, clearAuth]);

  useEffect(() => {
    if (token && teamId) {
      fetchTeam();
    }
  }, [token, teamId]);

  const fetchTeam = useCallback(async () => {
    if (!teamId) return;

    setLoading(true);
    setError('');
    try {
      const teamData = await teamAPI.getMyTeam(teamId);
      setTeam(teamData);
    } catch (err: any) {
      if (err.response?.status === 401) {
        clearAuth();
        setError('Session expired. Please login again.');
      } else {
        setError('Failed to load team data');
      }
    } finally {
      setLoading(false);
    }
  }, [teamId, clearAuth]);

  const handleAuth = useCallback(async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const data = isLogin
        ? await authAPI.login({ email, password })
        : await authAPI.register({
            email,
            password,
            fullName: fullName || email.split('@')[0],
            role: 'ROLE_USER'
          });

      setAuth(data.token, data.teamId, data.userId);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  }, [isLogin, email, password, fullName, setAuth]);

  const handleLogout = useCallback(() => {
    setTeam(null);
    clearAuth();
  }, [clearAuth]);

  const handlePlayerEdit = useCallback((player: Player) => {
    // handle player edit
  }, []);

  const handlePlayerSave = useCallback(async (playerId: string) => {
    // handle player save
  }, []);

  const handleTeamSave = useCallback(async () => {
    // handle team save
  }, []);

  const handleUserProfileUpdate = useCallback((updatedUser: User) => {
    // User profile updated successfully
    // You could update local state or show a success message here
  }, []);

  const handleUserDelete = useCallback(() => {
    // User deleted successfully, logout and clear auth
    handleLogout();
  }, [handleLogout]);

  const handleEditProfileClick = useCallback(() => {
    if (!userId) {
      setError('User ID not found. Please try logging out and logging back in.');
      return;
    }
    setIsUserProfileEditOpen(true);
  }, [userId]);

  if (!token) {
    return (
      <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", backgroundColor: "#f9fafb" }}>
        <div style={{ maxWidth: "400px", width: "100%", margin: "0 20px" }}>
          <div style={{ textAlign: "center", marginBottom: "30px" }}>
            <h2 style={{ fontSize: "24px", fontWeight: "bold", color: "#111827", marginBottom: "10px" }}>
              {isLogin ? "Sign in to Soccer Manager" : "Create your account"}
            </h2>
          </div>
          <form onSubmit={handleAuth} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            {!isLogin && (
              <input
                type="text"
                name="fullName"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="Full name"
                required
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px'
                }}
              />
            )}
            <input
              type="email"
              name="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Email address"
              required
              style={{
                width: '100%',
                padding: '12px',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '14px'
              }}
            />
            <input
              type="password"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              required
              style={{
                width: '100%',
                padding: '12px',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '14px'
              }}
            />

            {error && (
              <div style={{ color: '#dc2626', fontSize: '14px', textAlign: 'center' }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              style={{
                width: '100%',
                padding: '12px',
                backgroundColor: '#4f46e5',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                fontSize: '14px',
                fontWeight: '500',
                cursor: loading ? 'not-allowed' : 'pointer',
                opacity: loading ? 0.5 : 1
              }}
            >
              {loading ? 'Processing...' : (isLogin ? 'Sign in' : 'Sign up')}
            </button>

            <div style={{ textAlign: 'center' }}>
              <button
                type="button"
                onClick={() => setIsLogin(!isLogin)}
                style={{
                  background: 'none',
                  border: 'none',
                  color: '#4f46e5',
                  fontSize: '14px',
                  cursor: 'pointer',
                  textDecoration: 'underline'
                }}
              >
                {isLogin ? 'Need an account? Sign up' : 'Already have an account? Sign in'}
              </button>
            </div>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f3f4f6' }}>
      <nav style={{ backgroundColor: 'white', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', height: '64px' }}>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <h1 style={{ fontSize: '20px', fontWeight: 'bold', color: '#1f2937' }}>Soccer Manager</h1>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <span style={{ color: '#6b7280', lineHeight: '64px' }}>Team: {team?.name || 'Loading...'}</span>
              <button
                onClick={handleEditProfileClick}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  height: '64px',
                  color: '#6b7280',
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontFamily: 'inherit',
                  fontWeight: 'inherit',
                  textDecoration: 'none',
                  padding: '0',
                  margin: '0',
                  outline: 'none',
                  boxShadow: 'none',
                  verticalAlign: 'middle'
                }}
              >
                Edit Profile
              </button>
              <button
                onClick={handleLogout}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  height: '64px',
                  color: '#6b7280',
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontFamily: 'inherit',
                  fontWeight: 'inherit',
                  textDecoration: 'none',
                  padding: '0',
                  margin: '0',
                  outline: 'none',
                  boxShadow: 'none',
                  verticalAlign: 'middle'
                }}
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main style={containerStyle}>
        {loading && (
          <div style={{ textAlign: 'center', padding: '48px' }}>
            <div style={{ color: '#6b7280' }}>Loading team data...</div>
          </div>
        )}

        {error && (
          <div style={{ backgroundColor: '#fee2e2', border: '1px solid #fecaca', color: '#dc2626', padding: '12px', borderRadius: '6px', marginBottom: '24px' }}>
            {error}
          </div>
        )}

        {team && (
          <div>
            <div style={{ marginBottom: '24px', borderBottom: '1px solid #e5e7eb' }}>
              <div style={{ display: 'flex', gap: '32px' }}>
                <button
                  onClick={() => setActiveTab('dashboard')}
                  style={{
                    padding: '8px 0',
                    borderBottom: activeTab === 'dashboard' ? '2px solid #4f46e5' : '2px solid transparent',
                    borderTop: 'none',
                    borderRight: 'none',
                    borderLeft: 'none',
                    color: activeTab === 'dashboard' ? '#4f46e5' : '#6b7280',
                    background: 'none',
                    cursor: 'pointer',
                    fontSize: '14px',
                    fontWeight: '500'
                  }}
                >
                  Dashboard
                </button>
                <button
                  onClick={() => setActiveTab('transfers')}
                  style={{
                    padding: '8px 0',
                    borderBottom: activeTab === 'transfers' ? '2px solid #4f46e5' : '2px solid transparent',
                    borderTop: 'none',
                    borderRight: 'none',
                    borderLeft: 'none',
                    color: activeTab === 'transfers' ? '#4f46e5' : '#6b7280',
                    background: 'none',
                    cursor: 'pointer',
                    fontSize: '14px',
                    fontWeight: '500'
                  }}
                >
                  Transfer Market
                </button>
              </div>
            </div>

            {activeTab === 'dashboard' && team && (
              <Dashboard team={team} onTeamUpdate={setTeam} />
            )}

            {activeTab === 'transfers' && (
              <TransferMarket teamId={teamId!} teamPlayers={team?.players || []} />
            )}
          </div>
        )}
      </main>

      {userId && (
        <UserProfileEdit
          userId={userId}
          isOpen={isUserProfileEditOpen}
          onClose={() => setIsUserProfileEditOpen(false)}
          onUpdate={handleUserProfileUpdate}
          onDelete={handleUserDelete}
        />
      )}
    </div>
  );
}

export default App;