import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css'
})
export class Inicio {
  constructor(public authService: AuthService) {}

  get perfil(): string | undefined {
    return this.authService.currentUser()?.perfil;
  }
}