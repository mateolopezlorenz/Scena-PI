import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Local, CreateLocalRequest, UpdateLocalRequest } from '../models';

@Injectable({
  providedIn: 'root',
})
export class LocalService {

  private url = 'http://localhost:8080/api/locals';

  constructor(private http: HttpClient) {}

  // Método para crear un nuevo local.
  crearLocal(data: CreateLocalRequest): Observable<Local> {
    return this.http.post<Local>(this.url, data);
  }

  // Método para obtener la lista de locales.
  getLocals(): Observable<Local[]> {
    return this.http.get<Local[]>(this.url);
  }

  updateLocal(id: number, data: UpdateLocalRequest): Observable<Local> {
    return this.http.put<Local>(`${this.url}/${id}`, data);
  }

  // Método para eliminar un local.
  deleteLocal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
