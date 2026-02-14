export const authStorage = {
  getToken: (): string | null => localStorage.getItem("token"),
  getTeamId: (): string | null => localStorage.getItem("teamId"),
  
  setAuth: (token: string, teamId: string): void => {
    localStorage.setItem("token", token);
    localStorage.setItem("teamId", teamId);
  },
  
  clearAuth: (): void => {
    localStorage.removeItem("token");
    localStorage.removeItem("teamId");
  }
};
