import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';

export interface ReporteDTO {
  id?: number;
  nombre: string;
  fecha: Date;
  tipoMime?: string;
  tamanoArchivo?: number;
  nombreArchivoOriginal?: string;
  fechaCreacion?: Date;
  fechaEditado?: Date;
  creador?: {
    id: number;
    nombreUsuario: string;
    email: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private readonly baseUrl = 'http://localhost:8080/ProyectoASIS/api/reportes';
  
  // Signals para el estado del servicio
  private _reportes = signal<ReporteDTO[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string>('');

  // Signals públicos de solo lectura
  readonly reportes = this._reportes.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  constructor(private http: HttpClient) {}

  /**
   * Obtiene todos los reportes
   */
  getReportes(): Observable<ReporteDTO[]> {
    this._loading.set(true);
    this._error.set('');

    return this.http.get<ReporteDTO[]>(this.baseUrl).pipe(
      tap(reportes => {
        this._reportes.set(reportes);
        this._loading.set(false);
      }),
      catchError(error => {
        this._loading.set(false);
        this._error.set('Error al obtener reportes');
        console.error('Error al obtener reportes:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Sube un archivo como reporte
   */
  subirArchivo(archivo: File, creadorId: number): Observable<ReporteDTO> {
    this._loading.set(true);
    this._error.set('');

    const formData = new FormData();
    formData.append('file', archivo);
    formData.append('creadorId', creadorId.toString());

    return this.http.post<ReporteDTO>(`${this.baseUrl}/upload`, formData).pipe(
      tap(reporte => {
        // Agregar el nuevo reporte a la lista
        this._reportes.update(reportes => [...reportes, reporte]);
        this._loading.set(false);
      }),
      catchError(error => {
        this._loading.set(false);
        this._error.set('Error al subir archivo');
        console.error('Error al subir archivo:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Verifica si un usuario puede subir archivos/reportes
   */
  puedeSubirArchivos(usuarioActual: any): boolean {
    if (!usuarioActual) return false;
    
    // Solo PERSONAL_SALUD y ADMINISTRADOR pueden subir archivos
    return usuarioActual.perfil === 'PERSONAL_SALUD' || usuarioActual.perfil === 'ADMINISTRADOR';
  }

  /**
   * Descarga un archivo de reporte
   */
  descargarArchivo(id: number): Observable<Blob> {
    this._loading.set(true);
    this._error.set('');

    return this.http.get(`${this.baseUrl}/${id}/download`, {
      responseType: 'blob'
    }).pipe(
      tap(() => {
        this._loading.set(false);
      }),
      catchError(error => {
        this._loading.set(false);
        this._error.set('Error al descargar archivo');
        console.error('Error al descargar archivo:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Elimina un reporte con validación de permisos
   */
  eliminarReporte(id: number, usuarioId: number): Observable<any> {
    this._loading.set(true);
    this._error.set('');

    return this.http.delete(`${this.baseUrl}/${id}/usuario/${usuarioId}`).pipe(
      tap(() => {
        // Remover el reporte de la lista
        this._reportes.update(reportes => reportes.filter(r => r.id !== id));
        this._loading.set(false);
      }),
      catchError(error => {
        this._loading.set(false);
        this._error.set('Error al eliminar reporte');
        console.error('Error al eliminar reporte:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Verifica si un usuario puede eliminar un reporte específico
   */
  puedeEliminarReporte(reporte: ReporteDTO, usuarioActual: any): boolean {
    if (!usuarioActual || !reporte) return false;
    
    // El usuario puede eliminar si es el creador o es administrador
    const esCreador = reporte.creador?.id === usuarioActual.id;
    const esAdmin = usuarioActual.perfil === 'ADMINISTRADOR';
    
    return esCreador || esAdmin;
  }

  /**
   * Descarga un archivo y lo guarda en el dispositivo
   */
  descargarYGuardarArchivo(reporte: ReporteDTO): void {
    if (!reporte.id) return;

    this.descargarArchivo(reporte.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = reporte.nombreArchivoOriginal || reporte.nombre;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error al descargar archivo:', error);
      }
    });
  }

  /**
   * Formatea el tamaño del archivo en formato legible
   */
  formatearTamano(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * Limpia el error actual
   */
  clearError(): void {
    this._error.set('');
  }
}