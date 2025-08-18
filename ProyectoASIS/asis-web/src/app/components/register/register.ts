import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  userData: RegisterRequest = {
    nombreUsuario: '',
    email: '',
    contrasena: '',
    perfil: 'PERSONAL_SALUD'
  };

  errorMessage = signal<string>('');
  successMessage = signal<string>('');

  // Opciones de perfil disponibles (sin ADMINISTRADOR)
  perfilOptions = [
    { value: 'PERSONAL_SALUD', label: 'Personal de Salud' },
    { value: 'REFERENTE_ORG_SOCIAL', label: 'Referente Organización Social' }
  ];

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.userData.nombreUsuario || !this.userData.email || !this.userData.contrasena) {
      this.errorMessage.set('Por favor complete todos los campos');
      return;
    }

    // Validación básica de email
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(this.userData.email)) {
      this.errorMessage.set('Por favor ingrese un email válido');
      return;
    }

    // Validación de longitud mínima de contraseña
    if (this.userData.contrasena.length < 6) {
      this.errorMessage.set('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    // Validación de longitud mínima de nombre de usuario
    if (this.userData.nombreUsuario.length < 3) {
      this.errorMessage.set('El nombre de usuario debe tener al menos 3 caracteres');
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    
    this.authService.register(this.userData).subscribe({
      next: (response) => {
        console.log('Registro exitoso:', response.message);
        this.successMessage.set(response.message);
        // Limpiar formulario
        this.userData = {
          nombreUsuario: '',
          email: '',
          contrasena: '',
          perfil: 'PERSONAL_SALUD'
        };
        
        // Redirección automática después de 3 segundos
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        console.error('Error en registro:', error);
        if (error.status === 400 && error.error?.message) {
          // Mensajes específicos del backend
          this.errorMessage.set(error.error.message);
        } else if (error.error?.message) {
          this.errorMessage.set(error.error.message);
        } else {
          this.errorMessage.set('Error de conexión. Intente nuevamente.');
        }
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
