import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { HeaderComponent } from '../header/header';
import { HeroComponent } from '../hero/hero';
import { AboutComponent } from '../about/about';
import { ContactComponent } from '../contact/contact';
import { FooterComponent } from '../footer/footer';
import { ReporteService, ReporteDTO } from '../../services/reporte.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, HeaderComponent, HeroComponent, AboutComponent, ContactComponent, FooterComponent],
  templateUrl: './landing.html',
  styleUrls: ['./landing.css']
})
export class LandingComponent implements OnInit, OnDestroy {
  private _reportesPublicos = signal<ReporteDTO[]>([]);
  private _cargandoReportes = signal<boolean>(false);
  private _errorReportes = signal<string>('');
  
  readonly reportesPublicos = this._reportesPublicos.asReadonly();
  readonly cargandoReportes = this._cargandoReportes.asReadonly();
  readonly errorReportes = this._errorReportes.asReadonly();
  
  private subscriptions = new Subscription();

  constructor(
    private router: Router,
    private reporteService: ReporteService
  ) {}

  ngOnInit(): void {
    this.cargarReportesPublicos();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  cargarReportesPublicos(): void {
    this._cargandoReportes.set(true);
    this._errorReportes.set('');
    
    const sub = this.reporteService.getReportesPublicos().subscribe({
      next: (reportes) => {
        this._reportesPublicos.set(reportes);
        this._cargandoReportes.set(false);
      },
      error: (error) => {
        console.error('Error al cargar reportes públicos:', error);
        this._errorReportes.set('Error al cargar los reportes públicos');
        this._cargandoReportes.set(false);
      }
    });
    this.subscriptions.add(sub);
  }

  descargarReporte(reporte: ReporteDTO): void {
    this.reporteService.descargarYGuardarArchivo(reporte);
  }

  formatearTamano(bytes: number | undefined): string {
    if (!bytes) return '0 Bytes';
    return this.reporteService.formatearTamano(bytes);
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

  goToLogin() {
    this.router.navigate(['/login']);
  }

  goToContact() {
    // Scroll to contact section
    document.getElementById('contacto')?.scrollIntoView({ 
      behavior: 'smooth' 
    });
  }

  goToReportes() {
    // Scroll to reportes section
    document.getElementById('reportes')?.scrollIntoView({ 
      behavior: 'smooth' 
    });
  }
}