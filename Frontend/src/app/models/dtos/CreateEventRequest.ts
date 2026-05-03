//Datos necesarios para crear un nuevo evento.
export interface CreateEventRequest {
  name: string;
  description: string;
  category: string;
  startDate: string;
  endDate: string;
  latitude: number | null;
  longitude: number | null;
  address: string;
  localId: number | null;
  status: string;
}