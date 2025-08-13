import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Usuario {
  id?: number;
  nombreUsuario: string;
  email: string;
  contrasena: string;
  perfil: string;
  habilitado: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api/usuarios';

  // Signals para el estado global
  private _usuarios = signal<Usuario[]>([]);
  private _usuariosPendientes = signal<Usuario[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string | null>(null);

  // Signals públicos de solo lectura
  readonly usuarios = this._usuarios.asReadonly();
  readonly usuariosPendientes = this._usuariosPendientes.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  // Signal computado para estadísticas
  readonly totalUsuarios = computed(() => this._usuarios().length);
  readonly usuariosHabilitados = computed(() => 
    this._usuarios().filter(u => u.habilitado).length
  );
  readonly totalUsuariosPendientes = computed(() => this._usuariosPendientes().length);

  constructor(private http: HttpClient) {}

  getUsuarios(): Observable<Usuario[]> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<Usuario[]>(`${this.apiUrl}/activos`)
      .pipe(
        tap(usuarios => {
          this._usuarios.set(usuarios);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al obtener usuarios:', error);
          this._error.set('Error al cargar usuarios');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  crearUsuario(usuario: Usuario): Observable<Usuario> {
    this._loading.set(true);
    
    return this.http.post<Usuario>(this.apiUrl, usuario)
      .pipe(
        tap(nuevoUsuario => {
          // Actualizar el signal agregando el nuevo usuario
          this._usuarios.update(usuarios => [...usuarios, nuevoUsuario]);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al crear usuario:', error);
          this._error.set('Error al crear usuario');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  actualizarUsuario(id: number, usuario: Usuario): Observable<Usuario> {
    this._loading.set(true);
    
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, usuario)
      .pipe(
        tap(usuarioActualizado => {
          // Actualizar el signal reemplazando el usuario modificado
          this._usuarios.update(usuarios => 
            usuarios.map(u => u.id === id ? usuarioActualizado : u)
          );
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al actualizar usuario:', error);
          this._error.set('Error al actualizar usuario');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  eliminarUsuario(id: number): Observable<any> {
    this._loading.set(true);
    
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' })
      .pipe(
        tap(() => {
          // Actualizar el signal removiendo el usuario eliminado
          this._usuarios.update(usuarios => usuarios.filter(u => u.id !== id));
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al eliminar usuario:', error);
          this._error.set('Error al eliminar usuario');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  getUsuariosPendientes(): Observable<Usuario[]> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<Usuario[]>(`${this.apiUrl}/pendientes`)
      .pipe(
        tap(usuarios => {
          this._usuariosPendientes.set(usuarios);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al obtener usuarios pendientes:', error);
          this._error.set('Error al cargar usuarios pendientes');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  habilitarUsuario(id: number): Observable<any> {
    this._loading.set(true);
    
    return this.http.put(`${this.apiUrl}/${id}/habilitar`, {}, { responseType: 'text' })
      .pipe(
        tap(() => {
          // Remover el usuario de la lista de pendientes
          this._usuariosPendientes.update(usuarios => usuarios.filter(u => u.id !== id));
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al habilitar usuario:', error);
          this._error.set('Error al habilitar usuario');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  // Método para limpiar errores
  clearError() {
    this._error.set(null);
  }
}
