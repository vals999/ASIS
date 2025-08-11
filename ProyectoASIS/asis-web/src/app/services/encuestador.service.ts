import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Encuestador {
  id?: number;
  ocupacion: string;
  datosPersonales?: {
    id: number;
    nombre: string;
    apellido: string;
    dni: string;
    email: string;
    fechaNacimiento: string;
    telefono: string;
    direccion: string;
  };
  usuario?: {
    id: number;
    nombreUsuario: string;
  };
}

@Injectable({ providedIn: 'root' })
export class EncuestadorService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api/encuestadores';

  // Signals para el estado global
  private _encuestadores = signal<Encuestador[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string | null>(null);

  // Signals públicos de solo lectura
  readonly encuestadores = this._encuestadores.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  // Signal computado para estadísticas
  readonly totalEncuestadores = computed(() => this._encuestadores().length);

  constructor(private http: HttpClient) {}

  getEncuestadores(): Observable<Encuestador[]> {
    this._loading.set(true);
    this._error.set(null);

    return this.http.get<Encuestador[]>(`${this.apiUrl}/activos`).pipe(
      tap((encuestadores) => {
        this._encuestadores.set(encuestadores);
        this._loading.set(false);
      }),
      catchError((error) => {
        console.error('Error al obtener encuestadores:', error);
        this._error.set('Error al cargar encuestadores');
        this._loading.set(false);
        return throwError(() => error);
      })
    );
  }

  getEncuestador(id: number): Observable<Encuestador> {
    return this.http.get<Encuestador>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        console.error('Error al obtener encuestador:', error);
        return throwError(() => error);
      })
    );
  }

  crearEncuestador(encuestador: Encuestador): Observable<Encuestador> {
    this._loading.set(true);

    return this.http.post<Encuestador>(this.apiUrl, encuestador).pipe(
      tap((nuevoEncuestador) => {
        // Actualizar el signal agregando el nuevo encuestador
        this._encuestadores.update((encuestadores) => [...encuestadores, nuevoEncuestador]);
        this._loading.set(false);
      }),
      catchError((error) => {
        console.error('Error al crear encuestador:', error);
        this._error.set('Error al crear encuestador');
        this._loading.set(false);
        return throwError(() => error);
      })
    );
  }

  actualizarEncuestador(id: number, encuestador: Encuestador): Observable<Encuestador> {
    this._loading.set(true);

    return this.http.put<Encuestador>(`${this.apiUrl}/${id}`, encuestador).pipe(
      tap((encuestadorActualizado) => {
        // Actualizar el signal reemplazando el encuestador modificado
        this._encuestadores.update((encuestadores) =>
          encuestadores.map((e) => (e.id === id ? encuestadorActualizado : e))
        );
        this._loading.set(false);
      }),
      catchError((error) => {
        console.error('Error al actualizar encuestador:', error);
        this._error.set('Error al actualizar encuestador');
        this._loading.set(false);
        return throwError(() => error);
      })
    );
  }

  eliminarEncuestador(id: number): Observable<any> {
    this._loading.set(true);

    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' }).pipe(
      tap(() => {
        // Actualizar el signal removiendo el encuestador eliminado
        this._encuestadores.update((encuestadores) => encuestadores.filter((e) => e.id !== id));
        this._loading.set(false);
      }),
      catchError((error) => {
        console.error('Error al eliminar encuestador:', error);
        this._error.set('Error al eliminar encuestador');
        this._loading.set(false);
        return throwError(() => error);
      })
    );
  }

  recuperarEncuestador(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/recuperar`, {}).pipe(
      catchError((error) => {
        console.error('Error al recuperar encuestador:', error);
        return throwError(() => error);
      })
    );
  }

  // Método para limpiar errores
  clearError() {
    this._error.set(null);
  }
}
