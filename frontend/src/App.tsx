import React, { useState, useEffect, useCallback } from 'react';
import { authAPI, teamAPI, playerAPI, userAPI, User } from './services/api';
import { Team, Player } from './types';
import { Dashboard } from './components/Dashboard';
import { TransferMarket } from './components/TransferMarket';
import { UserProfileEdit } from './components/UserProfileEdit';
import { authStorage } from './utils/auth';

function App() {
  // Authentication state
  const [token, setToken] = useState<string | null>(null);
  const [teamId, setTeamId] = useState<string | null>(null);
  const [userId, setUserId] = useState<string | null>(null);

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
  const [editingPlayer, setEditingPlayer] = useState<string | null>(null);
  const [playerForm, setPlayerForm] = useState<Partial<Player>>({});
  const [teamForm, setTeamForm] = useState({ name: '', country: '' });

  useEffect(() => {
    const savedToken = authStorage.getToken();
    const savedTeamId = authStorage.getTeamId();
    const savedUserId = authStorage.getUserId();

    if (savedToken && savedTeamId) {
      setToken(savedToken);
      setTeamId(savedTeamId);
      if (savedUserId) {
        setUserId(savedUserId);
      }
    } else if (savedToken && !savedTeamId) {
      authStorage.clearAuth();
      setToken(null);
      setTeamId(null);
      setUserId(null);
    }
  }, []);

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
      setTeamForm({ name: teamData.name, country: teamData.country });
    } catch (err: any) {
      if (err.response?.status === 401) {
        authStorage.clearAuth();
        setToken(null);
        setTeamId(null);
        setUserId(null);
        setError('Session expired. Please login again.');
      } else {
        setError('Failed to load team data');
      }
    } finally {
      setLoading(false);
    }
  }, [teamId]);

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

      setToken(data.token);
      setTeamId(data.teamId);
      setUserId(data.userId);
      authStorage.setAuth(data.token, data.teamId, data.userId);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  }, [isLogin, email, password, fullName]);

  const handleLogout = useCallback(() => {
    setToken(null);
    setTeamId(null);
    setUserId(null);
    setTeam(null);
    authStorage.clearAuth();
  }, []);

  const handlePlayerEdit = useCallback((player: Player) => {
    setEditingPlayer(player.id);
    setPlayerForm({
      firstName: player.firstName,
      lastName: player.lastName,
      age: player.age,
      country: player.country,
      position: player.position,
    });
  }, []);

  const handlePlayerSave = useCallback(async (playerId: string) => {
    if (!teamId) return;

    setLoading(true);
    setError('');
    try {
      await playerAPI.updatePlayer(teamId, playerId, playerForm);
      await fetchTeam();
      setEditingPlayer(null);
      setPlayerForm({});
    } catch (err: any) {
      if (err.response?.status === 401) {
        setError('Authentication failed. Please log in again.');
        authStorage.clearAuth();
        setToken(null);
        setTeamId(null);
      } else {
        setError('Failed to update player');
      }
    } finally {
      setLoading(false);
    }
  }, [teamId, playerForm, fetchTeam]);

  const handleTeamSave = useCallback(async () => {
    if (!teamId) return;

    setLoading(true);
    setError('');
    try {
      await teamAPI.updateTeam(teamId, teamForm);
      await fetchTeam();
    } catch (err: any) {
      if (err.response?.status === 401) {
        setError('Authentication failed. Please log in again.');
        authStorage.clearAuth();
        setToken(null);
        setTeamId(null);
      } else {
        setError('Failed to update team');
      }
    } finally {
      setLoading(false);
    }
  }, [teamId, teamForm, fetchTeam]);

  const handleUserProfileUpdate = useCallback((updatedUser: User) => {
    // User profile updated successfully
    // You could update local state or show a success message here
  }, []);

  const handleEditProfileClick = useCallback(() => {
    if (!userId) {
      alert('User ID not found. Please try logging out and logging back in.');
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
              <div>
                <input
                  type="text"
                  required
                  placeholder="Full name"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '12px',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontSize: '14px'
                  }}
                />
              </div>
            )}
            <div>
              <input
                type="email"
                required
                placeholder="Email address"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px'
                }}
              />
            </div>
            <div>
              <input
                type="password"
                required
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px'
                }}
              />
            </div>

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

      <main style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
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
        />
      )}
    </div>
  );
}

export default App;