//Modelo de datos para locales
export interface Local {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  ubication: string;
  capacity: number;
  rooms: number;
}