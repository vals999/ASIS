import { Component, ElementRef, ViewChild, OnInit, OnDestroy, signal, computed, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ReporteService, ReporteDTO } from '../../services/reporte.service';
import { AuthService } from '../../services/auth.service';

declare var bootstrap: any;

@Component({
  selector: 'app-reportes',
  imports: [CommonModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css'
})
export class ReportesComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  
  // Signals para mensajes
  private _mensajeExito = signal<string>('');
  private _mensajeError = signal<string>('');
  
  // Signals públicos de solo lectura
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeError = this._mensajeError.asReadonly();

  // Signal computado para filtrar reportes
  readonly reportesFiltrados = computed(() => {
    return this.reporteService.reportes();
  });

  private subscriptions = new Subscription();

  constructor(
    public reporteService: ReporteService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarReportes();
  }

  ngAfterViewInit(): void {
    // Inicializar tooltips de Bootstrap
    if (typeof bootstrap !== 'undefined') {
      const tooltipTriggerList = Array.from(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
      tooltipTriggerList.forEach(tooltipTriggerEl => {
        new bootstrap.Tooltip(tooltipTriggerEl);
      });
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  cargarReportes(): void {
    const sub = this.reporteService.getReportes().subscribe({
      next: () => {
        // Los reportes se actualizan automáticamente en el service
      },
      error: (error) => {
        console.error('Error al cargar reportes:', error);
        this._mensajeError.set('Error al cargar los reportes');
      }
    });
    this.subscriptions.add(sub);
  }

  subirReporte(): void {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    const files = target.files;
    
    if (files && files.length > 0) {
      // Validar y subir cada archivo seleccionado
      for (let i = 0; i < files.length; i++) {
        const archivo = files[i];
        if (this.esArchivoPermitido(archivo)) {
          this.subirArchivo(archivo);
        } else {
          this._mensajeError.set(`El archivo "${archivo.name}" no es de un tipo permitido. Solo se permiten: PDF, Word (.doc/.docx), Excel (.xls/.xlsx), CSV y TXT`);
          setTimeout(() => this._mensajeError.set(''), 5000);
        }
      }
    }
    
    // Limpiar el input para permitir subir el mismo archivo nuevamente si es necesario
    target.value = '';
  }

  /**
   * Valida si el archivo es de un tipo permitido
   */
  private esArchivoPermitido(archivo: File): boolean {
    const extension = archivo.name.toLowerCase().split('.').pop();
    const tiposPermitidos = ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'csv', 'txt'];
    
    if (extension && tiposPermitidos.includes(extension)) {
      return true;
    }
    
    // También validar por tipo MIME
    const mimeTypesPermitidos = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'text/csv',
      'text/plain'
    ];
    
    return mimeTypesPermitidos.includes(archivo.type);
  }

  private subirArchivo(archivo: File): void {
    const currentUser = this.authService.currentUser();
    if (!currentUser?.id) {
      this._mensajeError.set('Usuario no autenticado');
      return;
    }

    // Verificar si el usuario puede subir archivos según su perfil
    if (!this.reporteService.puedeSubirArchivos(currentUser)) {
      this._mensajeError.set('Solo los usuarios de Personal de Salud y Administradores pueden subir archivos de reportes');
      return;
    }

    this._mensajeError.set('');
    this._mensajeExito.set('');
    
    const sub = this.reporteService.subirArchivo(archivo, currentUser.id).subscribe({
      next: (reporte) => {
        this._mensajeExito.set(`Archivo "${archivo.name}" subido exitosamente`);
        // El archivo se agrega automáticamente a la lista en el service
        setTimeout(() => this._mensajeExito.set(''), 3000);
      },
      error: (error) => {
        console.error('Error al subir archivo:', error);
        let mensajeError = 'Error desconocido';
        
        if (error.status === 403) {
          mensajeError = 'No tienes permisos para subir archivos';
        } else if (error.error?.error) {
          mensajeError = error.error.error;
        }
        
        this._mensajeError.set(`Error al subir "${archivo.name}": ${mensajeError}`);
        setTimeout(() => this._mensajeError.set(''), 5000);
      }
    });
    this.subscriptions.add(sub);
  }

  /**
   * Verifica si el usuario actual puede subir archivos según su perfil
   */
  puedeSubir(): boolean {
    const currentUser = this.authService.currentUser();
    return this.reporteService.puedeSubirArchivos(currentUser);
  }

  formatearTamano(bytes: number | undefined): string {
    if (!bytes) return '0 Bytes';
    return this.reporteService.formatearTamano(bytes);
  }

  eliminarReporte(reporte: ReporteDTO): void {
    if (!reporte.id) return;
    
    const currentUser = this.authService.currentUser();
    if (!currentUser?.id) {
      this._mensajeError.set('Usuario no autenticado');
      return;
    }

    // Verificar permisos antes de mostrar confirmación
    if (!this.reporteService.puedeEliminarReporte(reporte, currentUser)) {
      this._mensajeError.set('No tienes permisos para eliminar este reporte. Solo el creador o un administrador pueden eliminarlo.');
      return;
    }

    if (confirm(`¿Estás seguro de que deseas eliminar "${reporte.nombre}"?`)) {
      this._mensajeError.set('');
      this._mensajeExito.set('');
      
      const sub = this.reporteService.eliminarReporte(reporte.id, currentUser.id).subscribe({
        next: () => {
          this._mensajeExito.set(`Reporte "${reporte.nombre}" eliminado exitosamente`);
          setTimeout(() => this._mensajeExito.set(''), 3000);
        },
        error: (error) => {
          console.error('Error al eliminar reporte:', error);
          let mensajeError = 'Error desconocido';
          
          if (error.status === 403) {
            mensajeError = 'No tienes permisos para eliminar este reporte';
          } else if (error.error?.error) {
            mensajeError = error.error.error;
          }
          
          this._mensajeError.set(`Error al eliminar "${reporte.nombre}": ${mensajeError}`);
          setTimeout(() => this._mensajeError.set(''), 5000);
        }
      });
      this.subscriptions.add(sub);
    }
  }

  /**
   * Verifica si el usuario actual puede eliminar un reporte específico
   */
  puedeEliminar(reporte: ReporteDTO): boolean {
    const currentUser = this.authService.currentUser();
    return this.reporteService.puedeEliminarReporte(reporte, currentUser);
  }

  descargarReporte(reporte: ReporteDTO): void {
    this._mensajeError.set('');
    this.reporteService.descargarYGuardarArchivo(reporte);
  }

  clearError(): void {
    this._mensajeError.set('');
    this.reporteService.clearError();
  }

  clearSuccess(): void {
    this._mensajeExito.set('');
  }

  obtenerTipoArchivo(tipoMime: string | undefined): string {
    if (!tipoMime) return 'Desconocido';
    
    const tipos: { [key: string]: string } = {
      'application/pdf': 'PDF',
      'application/msword': 'Word',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'Word',
      'application/vnd.ms-excel': 'Excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'Excel',
      'text/csv': 'CSV',
      'image/jpeg': 'JPEG',
      'image/png': 'PNG',
      'text/plain': 'Texto'
    };
    
    return tipos[tipoMime] || tipoMime.split('/')[1]?.toUpperCase() || 'Desconocido';
  }

  obtenerIconoArchivo(tipoMime: string | undefined): string {
    if (!tipoMime) return 'bi-file-earmark';
    
    if (tipoMime.includes('pdf')) return 'bi-file-earmark-pdf';
    if (tipoMime.includes('word')) return 'bi-file-earmark-word';
    if (tipoMime.includes('excel') || tipoMime.includes('spreadsheet')) return 'bi-file-earmark-excel';
    if (tipoMime.includes('image')) return 'bi-file-earmark-image';
    if (tipoMime.includes('text')) return 'bi-file-earmark-text';
    
    return 'bi-file-earmark';
  }
}
