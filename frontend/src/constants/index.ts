// Essential constants only

export const API_BASE_URL = "http://localhost:8081";

export const POSITION_ORDER: Record<string, number> = {
  'GOALKEEPER': 0,
  'DEFENDER': 1,
  'MIDFIELDER': 2,
  'ATTACKER': 3,
  'goalkeeper': 0,
  'defender': 1,
  'midfielder': 2,
  'attacker': 3
} as const;
