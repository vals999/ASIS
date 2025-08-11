import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Zona {
  id?: number;
  nombre: string;
  geolocalizacion: string;
  barrio?: {
    id: number;
    nombre: string;
  };
}

@Injectable({ providedIn: 'root' })
export class ZonaService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api/zonas';

  // Signals para el estado global
  private _zonas = signal<Zona[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string | null>(null);

  // Signals públicos de solo lectura
  readonly zonas = this._zonas.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  // Signal computado para estadísticas
  readonly totalZonas = computed(() => this._zonas().length);

  constructor(private http: HttpClient) {}

  getZonas(): Observable<Zona[]> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<Zona[]>(`${this.apiUrl}/activas`)
      .pipe(
        tap(zonas => {
          this._zonas.set(zonas);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al obtener zonas:', error);
          this._error.set('Error al cargar zonas');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  getZona(id: number): Observable<Zona> {
    return this.http.get<Zona>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error al obtener zona:', error);
          return throwError(() => error);
        })
      );
  }

  crearZona(zona: Zona): Observable<Zona> {
    this._loading.set(true);
    
    return this.http.post<Zona>(this.apiUrl, zona)
      .pipe(
        tap(nuevaZona => {
          // Actualizar el signal agregando la nueva zona
          this._zonas.update(zonas => [...zonas, nuevaZona]);
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al crear zona:', error);
          this._error.set('Error al crear zona');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  actualizarZona(id: number, zona: Zona): Observable<Zona> {
    this._loading.set(true);
    
    return this.http.put<Zona>(`${this.apiUrl}/${id}`, zona)
      .pipe(
        tap(zonaActualizada => {
          // Actualizar el signal reemplazando la zona modificada
          this._zonas.update(zonas => 
            zonas.map(z => z.id === id ? zonaActualizada : z)
          );
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al actualizar zona:', error);
          this._error.set('Error al actualizar zona');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  eliminarZona(id: number): Observable<any> {
    this._loading.set(true);
    
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' })
      .pipe(
        tap(() => {
          // Actualizar el signal removiendo la zona eliminada
          this._zonas.update(zonas => zonas.filter(z => z.id !== id));
          this._loading.set(false);
        }),
        catchError(error => {
          console.error('Error al eliminar zona:', error);
          this._error.set('Error al eliminar zona');
          this._loading.set(false);
          return throwError(() => error);
        })
      );
  }

  recuperarZona(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/recuperar`, {})
      .pipe(
        catchError(error => {
          console.error('Error al recuperar zona:', error);
          return throwError(() => error);
        })
      );
  }

  // Método para limpiar errores
  clearError() {
    this._error.set(null);
  }
}
