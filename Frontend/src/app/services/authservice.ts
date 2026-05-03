import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginResponse, LoginRequest, MessageResponse } from '../models';

@Injectable({
  providedIn: 'root',
})
export class Authservice {

  isLoggedIn: boolean;
  private url = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {
    this.isLoggedIn = !!localStorage.getItem('token');
  }

  // Método para registrar un nuevo usuario.
  register(data: {name: string, email: string, password: string}) {
    return this.http.post<MessageResponse>(`${this.url}/register`, data);
  }

  // Método para iniciar sesión.
  login(data: LoginRequest) {
    return this.http.post<LoginResponse>(`${this.url}/login`, data);
  }

  // Método para verificar si el usuario está autenticado.
  isAuthenticated(): boolean {
    return this.isLoggedIn;
  }

  // Método para cerrar sesión.
  logout(): void {
    const guestLikes = localStorage.getItem('guest_liked_events');
    localStorage.clear();
    if (guestLikes) {
      localStorage.setItem('guest_liked_events', guestLikes);
    }
    this.isLoggedIn = false;
  }

  // Método para guardar los datos de sesión en localStorage después de un inicio de sesión exitoso.
  saveSession(response: LoginResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('name', response.name);
    localStorage.setItem('email', response.email);
    localStorage.setItem('id', response.id.toString());

    this.isLoggedIn = true;
  }
}
