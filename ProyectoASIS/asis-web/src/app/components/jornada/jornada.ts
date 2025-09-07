import { Component, OnInit, OnDestroy, signal, computed, effect } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JornadaService, Jornada } from '../../services/jornada.service';
import { CampaniaService } from '../../services/campania.service';

@Component({
  selector: 'app-jornada',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './jornada.html',
  styleUrls: ['./jornada.css']
})
export class JornadaComponent implements OnInit, OnDestroy {
  // Signals locales del componente
  private _search = signal<string>('');
  private _mostrarFormularioAlta = signal<boolean>(false);
  private _mensajeExito = signal<string>('');
  private _mensajeEdicion = signal<string>('');
  private _jornadaEditando = signal<number | null>(null);
  private _paginaActual = signal<number>(1);
  private _elementosPorPagina = signal<number>(10);
  
  // Signals públicos de solo lectura
  readonly search = this._search.asReadonly();
  readonly mostrarFormularioAlta = this._mostrarFormularioAlta.asReadonly();
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeEdicion = this._mensajeEdicion.asReadonly();
  readonly jornadaEditando = this._jornadaEditando.asReadonly();
  readonly paginaActual = this._paginaActual.asReadonly();
  readonly elementosPorPagina = this._elementosPorPagina.asReadonly();

  // Signals para formularios
  nuevaJornada = signal<Jornada>({
    fecha: '',
    campaña: undefined
  });

  jornadaEditada = signal<Jornada>({
    fecha: '',
    campaña: undefined
  });

  // Signal computado para filtrado
  readonly jornadasFiltradas = computed(() => {
    const jornadas = this.jornadaService.jornadas();
    const searchTerm = this._search().toLowerCase();
    
    if (!searchTerm) return jornadas;
    
    return jornadas.filter(j =>
      j.fecha.includes(searchTerm) ||
      (j.campaña?.nombre && j.campaña.nombre.toLowerCase().includes(searchTerm))
    );
  });

  // Signals computados para paginación
  readonly totalPaginas = computed(() => {
    const totalJornadas = this.jornadasFiltradas().length;
    return Math.ceil(totalJornadas / this._elementosPorPagina());
  });

  readonly jornadasPaginadas = computed(() => {
    const jornadas = this.jornadasFiltradas();
    const inicio = (this._paginaActual() - 1) * this._elementosPorPagina();
    const fin = inicio + this._elementosPorPagina();
    return jornadas.slice(inicio, fin);
  });

  readonly paginasArray = computed(() => {
    const total = this.totalPaginas();
    return Array.from({ length: total }, (_, i) => i + 1);
  });

  private routerSubscription: Subscription = new Subscription();

  constructor(
    public jornadaService: JornadaService,
    public campaniaService: CampaniaService,
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
    this.obtenerJornadas();
    this.obtenerCampanias();

    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects.includes('/jornadas')) {
        this.obtenerJornadas();
      }
    });
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }

  obtenerJornadas() {
    this.jornadaService.getJornadas().subscribe();
  }

  obtenerCampanias() {
    this.campaniaService.getCampanias().subscribe();
  }

  // Métodos para actualizar signals - NUEVA JORNADA
  updateSearch(value: string) {
    this._search.set(value);
    // Resetear a la primera página cuando se busca
    this._paginaActual.set(1);
  }

  toggleFormularioAlta() {
    this._mostrarFormularioAlta.update(show => !show);
  }

  updateNuevaJornadaFecha(value: string) {
    this.nuevaJornada.update(j => ({...j, fecha: value}));
  }

  updateNuevaJornadaCampania(value: string) {
    // Guardamos el ID de la campaña seleccionada
    const campaniaId = value ? Number(value) : undefined;
    const campania = campaniaId ? this.campaniaService.campanias().find(c => c.id === campaniaId) : undefined;
    
    this.nuevaJornada.update(j => ({
      ...j, 
      campaña: campania ? { 
        id: campania.id!, 
        nombre: campania.nombre, 
        fechaInicio: campania.fechaInicio, 
        fechaFin: campania.fechaFin 
      } : undefined
    }));
  }

  // Métodos para actualizar signals - JORNADA EDITADA
  updateJornadaEditadaFecha(value: string) {
    this.jornadaEditada.update(j => ({...j, fecha: value}));
  }

  updateJornadaEditadaCampania(value: string) {
    // Guardamos el ID de la campaña seleccionada
    const campaniaId = value ? Number(value) : undefined;
    const campania = campaniaId ? this.campaniaService.campanias().find(c => c.id === campaniaId) : undefined;
    
    this.jornadaEditada.update(j => ({
      ...j, 
      campaña: campania ? { 
        id: campania.id!, 
        nombre: campania.nombre, 
        fechaInicio: campania.fechaInicio, 
        fechaFin: campania.fechaFin 
      } : undefined
    }));
  }

  eliminarJornada(id?: number) {
    if (id === undefined) return;
    
    if (confirm('¿Seguro que deseas eliminar esta jornada?')) {
      this.jornadaService.eliminarJornada(id).subscribe({
        next: () => {
          this._mensajeExito.set('Jornada eliminada exitosamente');
        },
        error: (error) => {
          console.error('Error al eliminar jornada:', error);
          this._mensajeExito.set('Error al eliminar la jornada. Intenta nuevamente.');
        }
      });
    }
  }

  crearJornada() {
    const jornada = this.nuevaJornada();
    
    if (!jornada.fecha) {
      this._mensajeExito.set('Completa todos los campos');
      return;
    }
    
    this.jornadaService.crearJornada(jornada).subscribe({
      next: (nuevaJornadaCreada) => {
        this._mensajeExito.set('Jornada creada exitosamente');
        
        // Limpiar formulario
        this.nuevaJornada.set({
          fecha: '',
          campaña: undefined
        });
        
        // Limpiar búsqueda
        this._search.set('');
        
        // Cerrar el formulario de alta
        this._mostrarFormularioAlta.set(false);
        
        // Forzar actualización de la vista obteniendo las jornadas del servidor
        this.obtenerJornadas();
      },
      error: (error) => {
        console.error('Error al crear jornada:', error);
        this._mensajeExito.set('Error al crear la jornada. Intenta nuevamente.');
      }
    });
  }

  cancelarAlta() {
    this._mostrarFormularioAlta.set(false);
    this.nuevaJornada.set({
      fecha: '',
      campaña: undefined
    });
  }

  editarJornada(jornada: Jornada) {
    this._jornadaEditando.set(jornada.id || null);
    
    this.jornadaEditada.set({
      id: jornada.id,
      fecha: jornada.fecha,
      campaña: jornada.campaña
    });
  }

  confirmarEdicion() {
    const jornada = this.jornadaEditada();
    
    if (!jornada.fecha) {
      this._mensajeEdicion.set('Completa todos los campos obligatorios');
      return;
    }
    
    const jornadaEditandoId = this._jornadaEditando();
    if (jornadaEditandoId && jornada.id) {
      if (confirm('¿Seguro que deseas editar esta jornada?')) {
        this.jornadaService.actualizarJornada(jornada.id, jornada).subscribe({
          next: (jornadaActualizada) => {
            // Actualizar el formulario con los datos actualizados
            this.jornadaEditada.set({
              id: jornadaActualizada.id,
              fecha: jornadaActualizada.fecha,
              campaña: jornadaActualizada.campaña
            });
            
            this._mensajeEdicion.set('Jornada actualizada exitosamente');
          },
          error: (error) => {
            console.error('Error al actualizar jornada:', error);
            this._mensajeEdicion.set('Error al actualizar la jornada. Intenta nuevamente.');
          }
        });
      }
    }
  }

  cancelarEdicion() {
    this._jornadaEditando.set(null);
    this._mensajeEdicion.set('');
    this.jornadaEditada.set({
      fecha: '',
      campaña: undefined
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
