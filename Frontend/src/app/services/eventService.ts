import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Events, CreateEventRequest, UpdateEventRequest } from '../models';

@Injectable({
  providedIn: 'root',
})
export class EventService {

  private url = 'http://localhost:8080/api/events';

  constructor(private http: HttpClient) {}

  //Método que envía los datos registrados al backend para crear el evento.
  createEvent(data: CreateEventRequest): Observable<Events> {
    return this.http.post<Events>(`${this.url}`, data);
  }

  //Método para obtener todos los eventos.
  getAllEvents(): Observable<Events[]> {
    return this.http.get<Events[]>(`${this.url}`);
  }

  //Método para obtener eventos filtrados con query params.
  getFilteredEvents(filters: { category?: string; date?: string; search?: string }): Observable<Events[]> {
    let params = new HttpParams();
    if (filters.category) {
      params = params.set('category', filters.category);
    }
    if (filters.date) {
      params = params.set('date', filters.date);
    }
    if (filters.search) {
      params = params.set('search', filters.search);
    }
    return this.http.get<Events[]>(`${this.url}`, { params });
  }

  //Método para obtener un evento por id.
  getEventById(id: number): Observable<Events> {
    return this.http.get<Events>(`${this.url}/${id}`);
  }

  //Método para actualizar un evento.
  updateEvent(id: number, data: UpdateEventRequest): Observable<Events> {
    return this.http.put<Events>(`${this.url}/${id}`, data);
  }

  //Método para eliminar un evento.
  deleteEvent(id: number): Observable<any> {
    return this.http.delete(`${this.url}/${id}`, { responseType: 'text' });
  }
}
