import { Component, signal, OnInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';
import { CommonModule, DOCUMENT, isPlatformBrowser } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {
  protected title = 'asis-web';
  
  isUsuariosMenuOpen = signal<boolean>(false);
  isAbmlMenuOpen = signal<boolean>(false);

  constructor(
    public authService: AuthService, 
    private router: Router,
    @Inject(DOCUMENT) private document: Document,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    // Escuchar cambios de ruta para cerrar menús no relacionados
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.updateMenuStates(event.urlAfterRedirects);
      this.updateBodyClass(event.urlAfterRedirects);
    });
  }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.updateBodyClass(this.router.url);
    }
  }

  ngOnDestroy() {
    // Limpiar clases del body al destruir el componente
    if (isPlatformBrowser(this.platformId)) {
      this.document.body.classList.remove('landing-page');
    }
  }

  private updateBodyClass(currentUrl: string): void {
    if (!isPlatformBrowser(this.platformId)) return;
    
    // Agregar clase landing-page SOLO para la página de landing (que tiene header flotante)
    const isLandingRoute = currentUrl === '/' || currentUrl.includes('/landing');
    
    if (!this.authService.isAuthenticated() && isLandingRoute) {
      this.document.body.classList.add('landing-page');
    } else {
      this.document.body.classList.remove('landing-page');
    }
  }

  private updateMenuStates(currentUrl: string): void {
    // Cerrar menús si no estamos en una ruta relacionada
    if (!this.isUsuariosRoute() && this.isUsuariosMenuOpen()) {
      this.isUsuariosMenuOpen.set(false);
    }
    
    if (!this.isAbmlRoute() && this.isAbmlMenuOpen()) {
      this.isAbmlMenuOpen.set(false);
    }

    // Abrir automáticamente el menú correspondiente si estamos en una ruta relacionada
    if (this.isUsuariosRoute() && !this.isUsuariosMenuOpen()) {
      this.isUsuariosMenuOpen.set(true);
    }
    
    if (this.isAbmlRoute() && !this.isAbmlMenuOpen()) {
      this.isAbmlMenuOpen.set(true);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleUsuariosMenu(): void {
    const newState = !this.isUsuariosMenuOpen();
    
    // Cerrar el otro menú si está abierto
    if (newState && this.isAbmlMenuOpen()) {
      this.isAbmlMenuOpen.set(false);
    }
    
    this.isUsuariosMenuOpen.set(newState);
  }

  toggleAbmlMenu(): void {
    const newState = !this.isAbmlMenuOpen();
    
    // Cerrar el otro menú si está abierto
    if (newState && this.isUsuariosMenuOpen()) {
      this.isUsuariosMenuOpen.set(false);
    }
    
    this.isAbmlMenuOpen.set(newState);
  }

  isUsuariosRoute(): boolean {
    const currentUrl = this.router.url;
    return currentUrl.includes('/usuarios');
  }

  isAbmlRoute(): boolean {
    const currentUrl = this.router.url;
    return currentUrl.includes('/campanias') || 
           currentUrl.includes('/jornadas') || 
           currentUrl.includes('/barrios') || 
           currentUrl.includes('/zonas') || 
           currentUrl.includes('/encuestadores');
  }
}
