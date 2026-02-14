export const authStorage = {
  getToken: (): string | null => localStorage.getItem("token"),
  getTeamId: (): string | null => localStorage.getItem("teamId"),
  getUserId: (): string | null => localStorage.getItem("userId"),
  
  setAuth: (token: string, teamId: string, userId?: string): void => {
    localStorage.setItem("token", token);
    localStorage.setItem("teamId", teamId);
    if (userId) {
      localStorage.setItem("userId", userId);
    }
  },
  
  clearAuth: (): void => {
    localStorage.removeItem("token");
    localStorage.removeItem("teamId");
    localStorage.removeItem("userId");
  }
};
