import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  credentials: LoginRequest = {
    nombreUsuario: '',
    contrasena: ''
  };

  errorMessage = signal<string>('');

  constructor(
    public authService: AuthService, 
    private router: Router
  ) {}

  goToHome(): void {
    this.router.navigate(['/']);
  }

  onSubmit(): void {
    if (!this.credentials.nombreUsuario || !this.credentials.contrasena) {
      this.errorMessage.set('Por favor complete todos los campos');
      return;
    }

    this.errorMessage.set('');
    
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response.message);
        this.router.navigate(['/inicio']);
      },
      error: (error) => {
        console.error('Error en login:', error);
        if (error.status === 401 && error.error?.message) {
          // Mostrar el mensaje específico del backend (usuario no encontrado, contraseña incorrecta, usuario deshabilitado)
          this.errorMessage.set(error.error.message);
        } else if (error.status === 401) {
          // Fallback si no hay mensaje específico
          this.errorMessage.set('Usuario o contraseña incorrectos');
        } else if (error.error?.message) {
          this.errorMessage.set(error.error.message);
        } else {
          this.errorMessage.set('Error de conexión. Intente nuevamente.');
        }
      }
    });
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}
