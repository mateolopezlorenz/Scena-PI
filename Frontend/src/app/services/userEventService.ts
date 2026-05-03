import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LikeCountResponse } from '../models';

@Injectable({
  providedIn: 'root',
})
export class UserEventService {

  private eventsUrl = 'http://localhost:8080/api/events';
  private usersUrl = 'http://localhost:8080/api/users';
  private localStorageKey = 'guest_liked_events';

  constructor(private http: HttpClient) {}

  // Añadir evento a favoritos (usuario registrado)
  addLike(eventId: number): Observable<any> {
    return this.http.post<any>(`${this.eventsUrl}/${eventId}/like`, {});
  }

  // Eliminar evento de favoritos (usuario registrado)
  removeLike(eventId: number): Observable<any> {
    return this.http.delete<any>(`${this.eventsUrl}/${eventId}/like`);
  }

  // Contar likes de un evento (público)
  countLikes(eventId: number): Observable<LikeCountResponse> {
    return this.http.get<LikeCountResponse>(`${this.eventsUrl}/${eventId}/likes/count`);
  }

  // Obtener eventos favoritos del usuario autenticado
  getLikedByUser(): Observable<any[]> {
    return this.http.get<any[]>(`${this.usersUrl}/me/likes`);
  }

  // Métodos para manejar likes de invitados usando localStorage
  getGuestLikeCounts(): { [eventId: number]: number } {
    try {
      const stored = localStorage.getItem(this.localStorageKey);
      if (stored) {
        return JSON.parse(stored);
      }
    } catch (_) {}
    return {};
  }

  // Obtener el conteo de likes de invitados para un evento específico
  getGuestLikeCount(eventId: number): number {
    return this.getGuestLikeCounts()[eventId] || 0;
  }

  // Añadir un like de invitado para un evento específico
  addGuestLike(eventId: number): number {
    const counts = this.getGuestLikeCounts();
    counts[eventId] = (counts[eventId] || 0) + 1;
    localStorage.setItem(this.localStorageKey, JSON.stringify(counts));
    return counts[eventId];
  }
}
