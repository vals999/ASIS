import { Injectable, signal, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, map, finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

export interface LoginRequest {
  nombreUsuario: string;
  contrasena: string;
}

export interface RegisterRequest {
  nombreUsuario: string;
  email: string;
  contrasena: string;
  perfil: string;
  // Nuevos campos de datos personales
  nombre: string;
  apellido: string;
  edad: number;
  dni: string;
  genero: string;
}

export interface RegisterResponse {
  message: string;
  usuario: {
    nombreUsuario: string;
    email: string;
    perfil: string;
    habilitado: boolean;
  };
}

export interface LoginResponse {
  token: string;
  usuario: {
    id: number;
    nombreUsuario: string;
    email: string;
    perfil: string;
  };
  message: string;
}

export interface AuthUser {
  id: number;
  nombreUsuario: string;
  email: string;
  perfil: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api/auth';
  private tokenKey = 'asis_token';
  private userKey = 'asis_user';
  private isBrowser: boolean;

  // Signal para el estado de autenticación
  private _isAuthenticated = signal<boolean>(false);
  private _currentUser = signal<AuthUser | null>(null);
  private _loading = signal<boolean>(false);

  // Signals públicos de solo lectura
  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();
  readonly loading = this._loading.asReadonly();

  constructor(
    private http: HttpClient, 
    private router: Router,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    
    // Solo verificar token si estamos en el navegador
    if (this.isBrowser) {
      this._isAuthenticated.set(this.hasValidToken());
      this._currentUser.set(this.getUserFromStorage());
      this.checkTokenValidity();
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    this._loading.set(true);
    
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          // Guardar token y usuario en sessionStorage solo si estamos en el navegador
          // sessionStorage se borra cuando se cierra el navegador o se reinicia
          if (this.isBrowser) {
            sessionStorage.setItem(this.tokenKey, response.token);
            sessionStorage.setItem(this.userKey, JSON.stringify(response.usuario));
          }
          
          // Actualizar signals
          this._isAuthenticated.set(true);
          this._currentUser.set(response.usuario);
        }),
        finalize(() => this._loading.set(false))
      );
  }

  register(userData: RegisterRequest): Observable<RegisterResponse> {
    this._loading.set(true);
    
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, userData)
      .pipe(
        finalize(() => this._loading.set(false))
      );
  }

  logout(): void {
    // Limpiar sessionStorage solo si estamos en el navegador
    if (this.isBrowser) {
      sessionStorage.removeItem(this.tokenKey);
      sessionStorage.removeItem(this.userKey);
    }
    
    // Actualizar signals
    this._isAuthenticated.set(false);
    this._currentUser.set(null);
    
    // Redirigir al login
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (!this.isBrowser) return null;
    return sessionStorage.getItem(this.tokenKey);
  }

  private hasValidToken(): boolean {
    if (!this.isBrowser) return false;
    
    const token = this.getToken();
    if (!token) return false;
    
    try {
      // Decodificar JWT para verificar expiración
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch (error) {
      return false;
    }
  }

  private getUserFromStorage(): AuthUser | null {
    if (!this.isBrowser) return null;
    
    const userStr = sessionStorage.getItem(this.userKey);
    if (!userStr) return null;
    
    try {
      return JSON.parse(userStr);
    } catch (error) {
      return null;
    }
  }

  private checkTokenValidity(): void {
    if (!this.isBrowser) return;
    
    if (!this.hasValidToken()) {
      this.logout();
    }
  }

  // Método para refrescar el estado de autenticación
  refreshAuthState(): void {
    if (!this.isBrowser) return;
    
    this._isAuthenticated.set(this.hasValidToken());
    this._currentUser.set(this.getUserFromStorage());
  }
}
