import axios from "axios";
import { Team, Player, Transfer } from '../types';

const API_BASE_URL = "http://localhost:8081";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  role?: string;
}

export interface AuthResponse {
  userId: string;
  teamId: string;
  token: string;
}

const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post("/auth/token", credentials);
    return response.data;
  },
  
  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post("/users", userData);
    return response.data;
  },
};

export const teamAPI = {
  getMyTeam: async (teamId: string): Promise<Team> => {
    const response = await api.get(`/teams/${teamId}?with_players=true`);
    return response.data;
  },
  
  updateTeam: async (teamId: string, teamData: Partial<Team>): Promise<Team> => {
    const response = await api.patch(`/teams/${teamId}`, teamData);
    return response.data;
  },
};

export const playerAPI = {
  updatePlayer: async (teamId: string, playerId: string, playerData: Partial<Player>): Promise<Player> => {
    const response = await api.patch(`/teams/${teamId}/players/${playerId}`, playerData);
    return response.data;
  },
};

export const transferAPI = {
  getTransfers: async (): Promise<Transfer[]> => {
    const response = await api.get("/transfers");
    return response.data;
  },
  
  createTransfer: async (teamId: string, playerId: string, askPrice: number): Promise<Transfer> => {
    const response = await api.post(`/teams/${teamId}/transfers`, {
      playerId,
      askPrice: askPrice.toString(),
    });
    return response.data;
  },
  
  buyPlayer: async (teamId: string, transferId: string): Promise<void> => {
    await api.delete(`/teams/${teamId}/transfers/${transferId}`);
  },

  updateTransfer: async (teamId: string, transferId: string, askPrice: number): Promise<Transfer> => {
    const response = await api.patch(`/teams/${teamId}/transfers/${transferId}`, {
      askPrice: askPrice.toString(),
    });
    return response.data;
  },

  removeTransfer: async (teamId: string, playerId: string): Promise<void> => {
    await api.delete(`/teams/${teamId}/transfers/${playerId}`);
  },
};

export default api;
