import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Jornada {
  id?: number;
  fecha: string; // formato YYYY-MM-DD
  campaña?: {   // Estructura del CampañaSimpleDTO (con ñ para coincidir con el backend)
    id: number;
    nombre: string;
    fechaInicio: string;
    fechaFin: string;
  };
}

@Injectable({ providedIn: 'root' })
export class JornadaService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api/jornadas';

  // Signals para el estado global
  private _jornadas = signal<Jornada[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string | null>(null);

  // Signals públicos de solo lectura
  readonly jornadas = this._jornadas.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  // Signal computado para estadísticas
  readonly totalJornadas = computed(() => this._jornadas().length);

  constructor(private http: HttpClient) {}

  getJornadas(): Observable<Jornada[]> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<Jornada[]>(`${this.apiUrl}/activas`)
      .pipe(
        tap(jornadas => {
          this._jornadas.set(jornadas);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al obtener jornadas:', error);
          this._error.set('Error al cargar jornadas');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  getJornada(id: number): Observable<Jornada> {
    return this.http.get<Jornada>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error al obtener jornada:', error);
          return throwError(() => error);
        })
      );
  }

  crearJornada(jornada: Jornada): Observable<Jornada> {
    this._loading.set(true);
    
    // Preparar la jornada para envío - solo enviar el ID de la campaña
    const jornadaParaEnvio = {
      fecha: jornada.fecha,
      campaña: jornada.campaña ? { id: jornada.campaña.id } : null
    };
    
    return this.http.post<Jornada>(this.apiUrl, jornadaParaEnvio)
      .pipe(
        tap(nuevaJornada => {
          // Actualizar el signal agregando la nueva jornada
          this._jornadas.update(jornadas => [...jornadas, nuevaJornada]);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al crear jornada:', error);
          this._error.set('Error al crear jornada');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  actualizarJornada(id: number, jornada: Jornada): Observable<Jornada> {
    this._loading.set(true);
    
    // Preparar la jornada para envío - solo enviar el ID de la campaña
    const jornadaParaEnvio = {
      fecha: jornada.fecha,
      campaña: jornada.campaña ? { id: jornada.campaña.id } : null
    };
    
    return this.http.put<Jornada>(`${this.apiUrl}/${id}`, jornadaParaEnvio)
      .pipe(
        tap(jornadaActualizada => {
          // Actualizar el signal reemplazando la jornada modificada
          this._jornadas.update(jornadas => 
            jornadas.map(j => j.id === id ? jornadaActualizada : j)
          );
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al actualizar jornada:', error);
          this._error.set('Error al actualizar jornada');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  eliminarJornada(id: number): Observable<any> {
    this._loading.set(true);
    
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' })
      .pipe(
        tap(() => {
          // Actualizar el signal removiendo la jornada eliminada
          this._jornadas.update(jornadas => jornadas.filter(j => j.id !== id));
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al eliminar jornada:', error);
          this._error.set('Error al eliminar jornada');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  recuperarJornada(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/recuperar`, {})
      .pipe(
        catchError(error => {
          console.error('Error al recuperar jornada:', error);
          return throwError(() => error);
        })
      );
  }

  // Método para limpiar errores
  clearError() {
    this._error.set(null);
  }
}
