import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Campania {
  id?: number;
  nombre: string;
  fechaInicio: string; // formato YYYY-MM-DD
  fechaFin: string;    // formato YYYY-MM-DD
  barrio?: any;        // Opcional por ahora
}

@Injectable({ providedIn: 'root' })
export class CampaniaService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api/campanias';

  // Signals para el estado global
  private _campanias = signal<Campania[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string | null>(null);

  // Signals públicos de solo lectura
  readonly campanias = this._campanias.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  // Signal computado para estadísticas
  readonly totalCampanias = computed(() => this._campanias().length);

  constructor(private http: HttpClient) {}

  getCampanias(): Observable<Campania[]> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<Campania[]>(`${this.apiUrl}/activas`)
      .pipe(
        tap(campanias => {
          this._campanias.set(campanias);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al obtener campañas:', error);
          this._error.set('Error al cargar campañas');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  getCampania(id: number): Observable<Campania> {
    return this.http.get<Campania>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error al obtener campaña:', error);
          return throwError(() => error);
        })
      );
  }

  crearCampania(campania: Campania): Observable<Campania> {
    this._loading.set(true);
    
    return this.http.post<Campania>(this.apiUrl, campania)
      .pipe(
        tap(nuevaCampania => {
          // Actualizar el signal agregando la nueva campaña
          this._campanias.update(campanias => [...campanias, nuevaCampania]);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al crear campaña:', error);
          this._error.set('Error al crear campaña');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  actualizarCampania(id: number, campania: Campania): Observable<Campania> {
    this._loading.set(true);
    
    return this.http.put<Campania>(`${this.apiUrl}/${id}`, campania)
      .pipe(
        tap(campaniaActualizada => {
          // Actualizar el signal reemplazando la campaña modificada
          this._campanias.update(campanias => 
            campanias.map(c => c.id === id ? campaniaActualizada : c)
          );
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al actualizar campaña:', error);
          this._error.set('Error al actualizar campaña');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  eliminarCampania(id: number): Observable<any> {
    this._loading.set(true);
    
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' })
      .pipe(
        tap(() => {
          // Actualizar el signal removiendo la campaña eliminada
          this._campanias.update(campanias => campanias.filter(c => c.id !== id));
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al eliminar campaña:', error);
          this._error.set('Error al eliminar campaña');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  recuperarCampania(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/recuperar`, {})
      .pipe(
        catchError(error => {
          console.error('Error al recuperar campaña:', error);
          return throwError(() => error);
        })
      );
  }

  // Método para limpiar errores
  clearError() {
    this._error.set(null);
  }
}
