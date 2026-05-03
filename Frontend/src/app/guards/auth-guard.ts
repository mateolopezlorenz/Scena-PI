import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Authservice } from '../services/authservice';

//Guard que protege rutas privadas. Redirige a /login si el usuario no está autenticado.
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(Authservice) as Authservice;
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
