//Datos necesarios para actualizar un evento.
export interface UpdateEventRequest {
  name: string;
  description: string;
  category: string;
  startDate: string;
  endDate: string;
  latitude: number | null;
  longitude: number | null;
  address: string;
  localId: number | null;
}