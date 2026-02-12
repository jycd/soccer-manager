export interface User {
  id: string;
  email: string;
}

export interface Player {
  id: string;
  firstName: string;
  lastName: string;
  country: string;
  age: number;
  position: 'GOALKEEPER' | 'DEFENDER' | 'MIDFIELDER' | 'ATTACKER';
  marketValue: number;
  status: 'ACTIVE' | 'ON_TRANSFER_LIST';
  team?: {
    id: string;
    name: string;
    country: string;
  };
}

export interface Team {
  id: string;
  name: string;
  country: string;
  marketValue: string;
  budget: string;
  players: Player[];
}

export interface Transfer {
  id: string;
  player: Player;
  askPrice: string;
  status?: 'PENDING' | 'COMPLETED';
}