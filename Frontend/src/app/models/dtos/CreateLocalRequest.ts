//Datos necesarios para registrar un nuevo local.
export interface CreateLocalRequest {
  name: string;
  latitude: number;
  longitude: number;
  ubication: string;
  capacity: number;
  rooms: number;
}