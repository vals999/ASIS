import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css'
})
export class Inicio {
  // Estadísticas de ejemplo (en una app real vendrían del backend)
  stats = {
    usuariosTotales: 1247,
    campanasActivas: 8,
    encuestasCompletadas: 3429,
    zonasCubiertas: 15
  };

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  get perfil(): string | undefined {
    return this.authService.currentUser()?.perfil;
  }

  navigateTo(route: string) {
    this.router.navigate([route]);
  }
}