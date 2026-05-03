import { User } from './userModel';
import { Local } from './localModel';

//Modelo de datos para eventos
export interface Events {
  id: number;
  name: string;
  description: string;
  category: string;
  startDate: string;
  endDate: string;
  latitude: number;
  longitude: number;
  address: string;
  user: User;
  local: Local | null;
  status: string;
}

