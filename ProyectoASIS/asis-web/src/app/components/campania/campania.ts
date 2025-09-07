import { Component, OnInit, OnDestroy, signal, computed, effect } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CampaniaService, Campania } from '../../services/campania.service';
import { BarrioService } from '../../services/barrio.service';

@Component({
  selector: 'app-campania',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './campania.html',
  styleUrls: ['./campania.css']
})
export class CampaniaComponent implements OnInit, OnDestroy {
  // Signals locales del componente
  private _search = signal<string>('');
  private _mostrarFormularioAlta = signal<boolean>(false);
  private _mensajeExito = signal<string>('');
  private _mensajeEdicion = signal<string>('');
  private _campaniaEditando = signal<number | null>(null);
  private _paginaActual = signal<number>(1);
  private _elementosPorPagina = signal<number>(10);
  
  // Signals públicos de solo lectura
  readonly search = this._search.asReadonly();
  readonly mostrarFormularioAlta = this._mostrarFormularioAlta.asReadonly();
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeEdicion = this._mensajeEdicion.asReadonly();
  readonly campaniaEditando = this._campaniaEditando.asReadonly();
  readonly paginaActual = this._paginaActual.asReadonly();
  readonly elementosPorPagina = this._elementosPorPagina.asReadonly();

  // Signals para formularios
  nuevaCampania = signal<Campania>({
    nombre: '',
    fechaInicio: '',
    fechaFin: '',
    barrio: undefined
  });

  campaniaEditada = signal<Campania>({
    nombre: '',
    fechaInicio: '',
    fechaFin: '',
    barrio: undefined
  });

  // Signal computado para filtrado
  readonly campaniasFiltradas = computed(() => {
    const campanias = this.campaniaService.campanias();
    const searchTerm = this._search().toLowerCase();
    
    if (!searchTerm) return campanias;
    
    return campanias.filter(c =>
      c.nombre.toLowerCase().includes(searchTerm) ||
      c.fechaInicio.includes(searchTerm) ||
      c.fechaFin.includes(searchTerm) ||
      (c.barrio?.nombre && c.barrio.nombre.toLowerCase().includes(searchTerm))
    );
  });

  // Signals computados para paginación
  readonly totalPaginas = computed(() => {
    const totalCampanias = this.campaniasFiltradas().length;
    return Math.ceil(totalCampanias / this._elementosPorPagina());
  });

  readonly campaniasPaginadas = computed(() => {
    const campanias = this.campaniasFiltradas();
    const inicio = (this._paginaActual() - 1) * this._elementosPorPagina();
    const fin = inicio + this._elementosPorPagina();
    return campanias.slice(inicio, fin);
  });

  readonly paginasArray = computed(() => {
    const total = this.totalPaginas();
    return Array.from({ length: total }, (_, i) => i + 1);
  });

  private routerSubscription: Subscription = new Subscription();

  constructor(
    public campaniaService: CampaniaService,
    public barrioService: BarrioService,
    private router: Router
  ) {
    // Effect para auto-limpiar mensajes después de 3 segundos
    effect(() => {
      const mensaje = this._mensajeExito();
      if (mensaje) {
        setTimeout(() => this._mensajeExito.set(''), 3000);
      }
    });

    effect(() => {
      const mensaje = this._mensajeEdicion();
      if (mensaje) {
        setTimeout(() => this._mensajeEdicion.set(''), 3000);
      }
    });
  }

  ngOnInit() {
    this.obtenerCampanias();
    this.obtenerBarrios();

    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects.includes('/campanias')) {
        this.obtenerCampanias();
      }
    });
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }

  obtenerCampanias() {
    this.campaniaService.getCampanias().subscribe();
  }

  obtenerBarrios() {
    this.barrioService.getBarrios().subscribe();
  }

  // Métodos para actualizar signals - NUEVA CAMPAÑA
  updateSearch(value: string) {
    this._search.set(value);
    // Resetear a la primera página cuando se busca
    this._paginaActual.set(1);
  }

  toggleFormularioAlta() {
    this._mostrarFormularioAlta.update(show => !show);
  }

  updateNuevaCampaniaNombre(value: string) {
    this.nuevaCampania.update(c => ({...c, nombre: value}));
  }

  updateNuevaCampaniaFechaInicio(value: string) {
    this.nuevaCampania.update(c => ({...c, fechaInicio: value}));
  }

  updateNuevaCampaniaFechaFin(value: string) {
    this.nuevaCampania.update(c => ({...c, fechaFin: value}));
  }

  updateNuevaCampaniaBarrio(value: string) {
    // Guardamos el ID del barrio seleccionado
    const barrioId = value ? Number(value) : undefined;
    const barrio = barrioId ? this.barrioService.barrios().find(b => b.id === barrioId) : undefined;
    
    this.nuevaCampania.update(c => ({
      ...c, 
      barrio: barrio ? { id: barrio.id!, nombre: barrio.nombre } : undefined
    }));
  }

  // Métodos para actualizar signals - CAMPAÑA EDITADA
  updateCampaniaEditadaNombre(value: string) {
    this.campaniaEditada.update(c => ({...c, nombre: value}));
  }

  updateCampaniaEditadaFechaInicio(value: string) {
    this.campaniaEditada.update(c => ({...c, fechaInicio: value}));
  }

  updateCampaniaEditadaFechaFin(value: string) {
    this.campaniaEditada.update(c => ({...c, fechaFin: value}));
  }

  updateCampaniaEditadaBarrio(value: string) {
    // Guardamos el ID del barrio seleccionado
    const barrioId = value ? Number(value) : undefined;
    const barrio = barrioId ? this.barrioService.barrios().find(b => b.id === barrioId) : undefined;
    
    this.campaniaEditada.update(c => ({
      ...c, 
      barrio: barrio ? { id: barrio.id!, nombre: barrio.nombre } : undefined
    }));
  }

  eliminarCampania(id?: number) {
    if (id === undefined) return;
    
    if (confirm('¿Seguro que deseas eliminar esta campaña?')) {
      this.campaniaService.eliminarCampania(id).subscribe({
        next: () => {
          this._mensajeExito.set('Campaña eliminada exitosamente');
        },
        error: (error) => {
          console.error('Error al eliminar campaña:', error);
          this._mensajeExito.set('Error al eliminar la campaña. Intenta nuevamente.');
        }
      });
    }
  }

  crearCampania() {
    const campania = this.nuevaCampania();
    
    if (!campania.nombre || !campania.fechaInicio || !campania.fechaFin) {
      this._mensajeExito.set('Completa todos los campos');
      return;
    }
    
    this.campaniaService.crearCampania(campania).subscribe({
      next: () => {
        this._mensajeExito.set('Campaña creada exitosamente');
        
        // Limpiar formulario
        this.nuevaCampania.set({
          nombre: '',
          fechaInicio: '',
          fechaFin: '',
          barrio: undefined
        });
        
        // Limpiar búsqueda
        this._search.set('');
      },
      error: (error) => {
        console.error('Error al crear campaña:', error);
        this._mensajeExito.set('Error al crear la campaña. Intenta nuevamente.');
      }
    });
  }

  cancelarAlta() {
    this._mostrarFormularioAlta.set(false);
    this.nuevaCampania.set({
      nombre: '',
      fechaInicio: '',
      fechaFin: '',
      barrio: undefined
    });
  }

  editarCampania(campania: Campania) {
    this._campaniaEditando.set(campania.id || null);
    
    this.campaniaEditada.set({
      id: campania.id,
      nombre: campania.nombre,
      fechaInicio: campania.fechaInicio,
      fechaFin: campania.fechaFin,
      barrio: campania.barrio
    });
  }

  confirmarEdicion() {
    const campania = this.campaniaEditada();
    
    if (!campania.nombre || !campania.fechaInicio || !campania.fechaFin) {
      this._mensajeEdicion.set('Completa todos los campos obligatorios');
      return;
    }
    
    const campaniaEditandoId = this._campaniaEditando();
    if (campaniaEditandoId && campania.id) {
      if (confirm('¿Seguro que deseas editar esta campaña?')) {
        this.campaniaService.actualizarCampania(campania.id, campania).subscribe({
          next: (campaniaActualizada) => {
            // Actualizar el formulario con los datos actualizados
            this.campaniaEditada.set({
              id: campaniaActualizada.id,
              nombre: campaniaActualizada.nombre,
              fechaInicio: campaniaActualizada.fechaInicio,
              fechaFin: campaniaActualizada.fechaFin
            });
            
            this._mensajeEdicion.set('Campaña actualizada exitosamente');
          },
          error: (error) => {
            console.error('Error al actualizar campaña:', error);
            this._mensajeEdicion.set('Error al actualizar la campaña. Intenta nuevamente.');
          }
        });
      }
    }
  }

  cancelarEdicion() {
    this._campaniaEditando.set(null);
    this._mensajeEdicion.set('');
    this.campaniaEditada.set({
      nombre: '',
      fechaInicio: '',
      fechaFin: '',
      barrio: undefined
    });
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
}
