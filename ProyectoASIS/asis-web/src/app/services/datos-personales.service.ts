import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, of } from 'rxjs';

export interface DatosPersonales {
  id?: number;
  nombre: string;
  apellido: string;
  dni: string;
  edad: number;
  genero: string;
}

@Injectable({ providedIn: 'root' })
export class DatosPersonalesService {
  private readonly API_URL = 'http://localhost:8080/ProyectoASIS/api/personas';
  
  // Signals para manejo de estado
  private _loading = signal(false);
  private _error = signal<string | null>(null);
  
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  constructor(private http: HttpClient) {}

  // Obtener datos personales por usuario ID
  getDatosPersonalesByUsuarioId(usuarioId: number): Observable<DatosPersonales> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<DatosPersonales>(`${this.API_URL}/usuario/${usuarioId}`).pipe(
      tap(() => this._loading.set(false)),
      catchError(error => {
        this._loading.set(false);
        this._error.set('Error al obtener los datos personales');
        console.error('Error al obtener datos personales:', error);
        throw error;
      })
    );
  }

  // Actualizar datos personales por usuario ID
  updateDatosPersonalesByUsuarioId(usuarioId: number, datos: DatosPersonales): Observable<DatosPersonales> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.put<DatosPersonales>(`${this.API_URL}/usuario/${usuarioId}`, datos).pipe(
      tap(() => this._loading.set(false)),
      catchError(error => {
        this._loading.set(false);
        this._error.set('Error al actualizar los datos personales');
        console.error('Error al actualizar datos personales:', error);
        throw error;
      })
    );
  }

  // Método para limpiar errores
  clearError() {
    this._error.set(null);
  }
}