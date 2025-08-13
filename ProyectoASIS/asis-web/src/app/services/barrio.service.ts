import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Barrio {
  id?: number;
  nombre: string;
  geolocalizacion: string;
}

@Injectable({ providedIn: 'root' })
export class BarrioService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api/barrios';

  // Signals para el estado global
  private _barrios = signal<Barrio[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string | null>(null);

  // Signals públicos de solo lectura
  readonly barrios = this._barrios.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  // Signal computado para estadísticas
  readonly totalBarrios = computed(() => this._barrios().length);

  constructor(private http: HttpClient) {}

  getBarrios(): Observable<Barrio[]> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<Barrio[]>(`${this.apiUrl}/activos`)
      .pipe(
        tap(barrios => {
          this._barrios.set(barrios);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al obtener barrios:', error);
          this._error.set('Error al cargar barrios');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  getBarrio(id: number): Observable<Barrio> {
    return this.http.get<Barrio>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error al obtener barrio:', error);
          return throwError(() => error);
        })
      );
  }

  crearBarrio(barrio: Barrio): Observable<Barrio> {
    this._loading.set(true);
    
    return this.http.post<Barrio>(this.apiUrl, barrio)
      .pipe(
        tap(nuevoBarrio => {
          // Actualizar el signal agregando el nuevo barrio
          this._barrios.update(barrios => [...barrios, nuevoBarrio]);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al crear barrio:', error);
          this._error.set('Error al crear barrio');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  actualizarBarrio(id: number, barrio: Barrio): Observable<Barrio> {
    this._loading.set(true);
    
    return this.http.put<Barrio>(`${this.apiUrl}/${id}`, barrio)
      .pipe(
        tap(barrioActualizado => {
          // Actualizar el signal reemplazando el barrio modificado
          this._barrios.update(barrios => 
            barrios.map(b => b.id === id ? barrioActualizado : b)
          );
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al actualizar barrio:', error);
          this._error.set('Error al actualizar barrio');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  eliminarBarrio(id: number): Observable<any> {
    this._loading.set(true);
    
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' })
      .pipe(
        tap(() => {
          // Actualizar el signal removiendo el barrio eliminado
          this._barrios.update(barrios => barrios.filter(b => b.id !== id));
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al eliminar barrio:', error);
          this._error.set('Error al eliminar barrio');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  recuperarBarrio(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/recuperar`, {})
      .pipe(
        catchError(error => {
          console.error('Error al recuperar barrio:', error);
          return throwError(() => error);
        })
      );
  }

  // Método para limpiar errores
  clearError() {
    this._error.set(null);
  }
}
