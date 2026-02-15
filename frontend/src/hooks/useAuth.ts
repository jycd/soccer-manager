import { useState, useEffect, useCallback } from 'react';
import { authStorage } from '../utils/auth';

interface AuthState {
  token: string | null;
  teamId: string | null;
  userId: string | null;
  isAuthenticated: boolean;
}

export const useAuth = () => {
  const [authState, setAuthState] = useState<AuthState>({
    token: null,
    teamId: null,
    userId: null,
    isAuthenticated: false
  });

  const setAuth = useCallback((token: string, teamId: string, userId: string) => {
    authStorage.setAuth(token, teamId, userId);
    setAuthState({
      token,
      teamId,
      userId,
      isAuthenticated: true
    });
  }, []);

  const clearAuth = useCallback(() => {
    authStorage.clearAuth();
    setAuthState({
      token: null,
      teamId: null,
      userId: null,
      isAuthenticated: false
    });
  }, []);

  useEffect(() => {
    const savedToken = authStorage.getToken();
    const savedTeamId = authStorage.getTeamId();
    const savedUserId = authStorage.getUserId();

    if (savedToken && savedTeamId) {
      setAuthState({
        token: savedToken,
        teamId: savedTeamId,
        userId: savedUserId || null,
        isAuthenticated: true
      });
    } else if (savedToken && !savedTeamId) {
      // Invalid state - token but no teamId
      clearAuth();
    }
  }, [clearAuth]);

  return {
    ...authState,
    setAuth,
    clearAuth
  };
};
