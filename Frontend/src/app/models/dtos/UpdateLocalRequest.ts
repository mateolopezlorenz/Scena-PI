//Datos necesarios para actualizar la información de un local.
export interface UpdateLocalRequest {
  name: string;
  latitude: number;
  longitude: number;
  ubication: string;
  capacity: number;
  rooms: number;
}