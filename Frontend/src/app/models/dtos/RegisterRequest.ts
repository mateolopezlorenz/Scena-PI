//Datos necesarios para registrar un nuevo usuario.
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  password2: string;
}