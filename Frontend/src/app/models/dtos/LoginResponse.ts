// Respuesta del inicio de sesión, contiene el token JWT e información del usuario.
export interface LoginResponse {
  message: string;
  token: string;
  name: string;
  email: string;
  id: number;
}