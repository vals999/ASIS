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
  private _paginaActual = signal<number>(1);
  private _elementosPorPagina = signal<number>(10);
  private _filtroVisibilidad = signal<string>('todos'); // 'todos', 'publicos', 'privados'
  
  // Signals públicos de solo lectura
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeError = this._mensajeError.asReadonly();
  readonly paginaActual = this._paginaActual.asReadonly();
  readonly elementosPorPagina = this._elementosPorPagina.asReadonly();
  readonly filtroVisibilidad = this._filtroVisibilidad.asReadonly();

  // Signal computado para filtrar reportes
  readonly reportesFiltrados = computed(() => {
    const reportes = this.reporteService.reportes();
    const filtro = this._filtroVisibilidad();
    
    if (filtro === 'publicos') {
      return reportes.filter(r => r.visibilidad === 'PUBLICO');
    } else if (filtro === 'privados') {
      return reportes.filter(r => r.visibilidad === 'PRIVADO');
    }
    return reportes;
  });

  // Signals computados para paginación
  readonly totalPaginas = computed(() => {
    const totalReportes = this.reportesFiltrados().length;
    return Math.ceil(totalReportes / this._elementosPorPagina());
  });

  readonly reportesPaginados = computed(() => {
    const reportes = this.reportesFiltrados();
    const inicio = (this._paginaActual() - 1) * this._elementosPorPagina();
    const fin = inicio + this._elementosPorPagina();
    return reportes.slice(inicio, fin);
  });

  readonly paginasArray = computed(() => {
    const total = this.totalPaginas();
    return Array.from({ length: total }, (_, i) => i + 1);
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

  cambiarFiltroVisibilidad(filtro: string): void {
    this._filtroVisibilidad.set(filtro);
    this._paginaActual.set(1); // Resetear a la primera página
  }

  cambiarVisibilidadReporte(reporte: ReporteDTO, nuevaVisibilidad: string): void {
    if (!reporte.id) return;
    
    const currentUser = this.authService.currentUser();
    if (!currentUser?.id) {
      this._mensajeError.set('Usuario no autenticado');
      return;
    }

    // Verificar permisos
    if (!this.puedeModificarVisibilidad(reporte)) {
      this._mensajeError.set('No tienes permisos para cambiar la visibilidad de este reporte');
      return;
    }

    this._mensajeError.set('');
    this._mensajeExito.set('');
    
    const sub = this.reporteService.cambiarVisibilidad(reporte.id, nuevaVisibilidad, currentUser.id).subscribe({
      next: (reporteActualizado) => {
        this._mensajeExito.set(`Visibilidad del reporte "${reporte.nombre}" cambiada a ${nuevaVisibilidad.toLowerCase()}`);
        setTimeout(() => this._mensajeExito.set(''), 3000);
      },
      error: (error) => {
        console.error('Error al cambiar visibilidad:', error);
        let mensajeError = 'Error desconocido';
        
        if (error.status === 403) {
          mensajeError = 'No tienes permisos para cambiar la visibilidad';
        } else if (error.error?.error) {
          mensajeError = error.error.error;
        }
        
        this._mensajeError.set(`Error al cambiar visibilidad: ${mensajeError}`);
        setTimeout(() => this._mensajeError.set(''), 5000);
      }
    });
    this.subscriptions.add(sub);
  }

  puedeModificarVisibilidad(reporte: ReporteDTO): boolean {
    const currentUser = this.authService.currentUser();
    if (!currentUser) return false;

    // Solo el creador o un administrador pueden cambiar la visibilidad
    const esCreador = reporte.creador?.id === currentUser.id;
    const esAdmin = currentUser.perfil === 'ADMINISTRADOR';
    
    return esCreador || esAdmin;
  }

  obtenerClaseVisibilidad(visibilidad: string | undefined): string {
    return visibilidad === 'PUBLICO' ? 'badge bg-success' : 'badge bg-secondary';
  }

  obtenerIconoVisibilidad(visibilidad: string | undefined): string {
    return visibilidad === 'PUBLICO' ? 'bi-globe' : 'bi-lock';
  }

  obtenerTextoVisibilidad(visibilidad: string | undefined): string {
    return visibilidad === 'PUBLICO' ? 'Público' : 'Privado';
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

  // Métodos para paginación
  irAPagina(pagina: number) {
    if (pagina >= 1 && pagina <= this.totalPaginas()) {
      this._paginaActual.set(pagina);
    }
  }

  paginaAnterior() {
    if (this._paginaActual() > 1) {
      this._paginaActual.update(p => p - 1);
    }
  }

  paginaSiguiente() {
    if (this._paginaActual() < this.totalPaginas()) {
      this._paginaActual.update(p => p + 1);
    }
  }

  // Métodos para contar reportes por visibilidad
  contarReportesPublicos(): number {
    return this.reporteService.reportes().filter(r => r.visibilidad === 'PUBLICO').length;
  }

  contarReportesPrivados(): number {
    return this.reporteService.reportes().filter(r => r.visibilidad === 'PRIVADO').length;
  }
}
