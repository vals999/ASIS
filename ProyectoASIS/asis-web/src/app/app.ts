import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'asis-web';
  
  isUsuariosMenuOpen = signal<boolean>(false);

  constructor(public authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout();
  }

  toggleUsuariosMenu(): void {
    this.isUsuariosMenuOpen.update(value => !value);
  }

  isUsuariosRoute(): boolean {
    const currentUrl = this.router.url;
    return currentUrl.includes('/usuarios');
  }
}
