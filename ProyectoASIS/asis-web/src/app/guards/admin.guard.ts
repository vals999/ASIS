import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const currentUser = authService.currentUser();
  
  // Verificar si el usuario está autenticado y es administrador
  if (currentUser && currentUser.perfil === 'ADMINISTRADOR') {
    return true;
  }
  
  // Si no es admin, redirigir a la página principal o mostrar error
  router.navigate(['/usuarios']);
  return false;
};
