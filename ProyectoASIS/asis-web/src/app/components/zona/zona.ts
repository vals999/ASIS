import { Component, OnInit, OnDestroy, signal, computed, effect } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ZonaService, Zona } from '../../services/zona.service';
import { BarrioService } from '../../services/barrio.service';
import { CoordenadasSelectorComponent } from './coordenadas-selector.component';

@Component({
  selector: 'app-zona',
  standalone: true,
  imports: [CommonModule, FormsModule, CoordenadasSelectorComponent],
  templateUrl: './zona.html',
  styleUrls: ['./zona.css']
})
export class ZonaComponent implements OnInit, OnDestroy {
  // Signals locales del componente
  private _search = signal<string>('');
  private _mostrarFormularioAlta = signal<boolean>(false);
  private _mensajeExito = signal<string>('');
  private _mensajeEdicion = signal<string>('');
  private _zonaEditando = signal<number | null>(null);
  private _mostrarSelectorCoordenadas = signal<boolean>(false);
  private _coordenadasEditando = signal<'nueva' | 'editada' | null>(null);
  
  // Signals públicos de solo lectura
  readonly search = this._search.asReadonly();
  readonly mostrarFormularioAlta = this._mostrarFormularioAlta.asReadonly();
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeEdicion = this._mensajeEdicion.asReadonly();
  readonly zonaEditando = this._zonaEditando.asReadonly();
  readonly mostrarSelectorCoordenadas = this._mostrarSelectorCoordenadas.asReadonly();

  // Signals para formularios
  nuevaZona = signal<Zona>({
    nombre: '',
    geolocalizacion: '',
    barrio: undefined
  });

  zonaEditada = signal<Zona>({
    nombre: '',
    geolocalizacion: '',
    barrio: undefined
  });

  // Signal computado para filtrado
  readonly zonasFiltradas = computed(() => {
    const zonas = this.zonaService.zonas();
    const searchTerm = this._search().toLowerCase();
    
    if (!searchTerm) return zonas;
    
    return zonas.filter(z =>
      z.nombre.toLowerCase().includes(searchTerm) ||
      z.geolocalizacion.toLowerCase().includes(searchTerm) ||
      (z.barrio?.nombre && z.barrio.nombre.toLowerCase().includes(searchTerm))
    );
  });

  private routerSubscription: Subscription = new Subscription();

  constructor(
    public zonaService: ZonaService,
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
    this.obtenerZonas();
    this.obtenerBarrios();

    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects.includes('/zonas')) {
        this.obtenerZonas();
      }
    });
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }

  obtenerZonas() {
    this.zonaService.getZonas().subscribe();
  }

  obtenerBarrios() {
    this.barrioService.getBarrios().subscribe();
  }

  // Métodos para actualizar signals - NUEVA ZONA
  updateSearch(value: string) {
    this._search.set(value);
  }

  toggleFormularioAlta() {
    this._mostrarFormularioAlta.update(show => !show);
  }

  updateNuevaZonaNombre(value: string) {
    this.nuevaZona.update(z => ({...z, nombre: value}));
  }

  updateNuevaZonaGeolocalizacion(value: string) {
    this.nuevaZona.update(z => ({...z, geolocalizacion: value}));
  }

  updateNuevaZonaBarrio(value: string) {
    // Por ahora guardamos el ID del barrio seleccionado
    const barrioId = value ? Number(value) : undefined;
    const barrio = barrioId ? this.barrioService.barrios().find(b => b.id === barrioId) : undefined;
    
    this.nuevaZona.update(z => ({
      ...z, 
      barrio: barrio ? { id: barrio.id!, nombre: barrio.nombre } : undefined
    }));
  }

  // Métodos para actualizar signals - ZONA EDITADA
  updateZonaEditadaNombre(value: string) {
    this.zonaEditada.update(z => ({...z, nombre: value}));
  }

  updateZonaEditadaGeolocalizacion(value: string) {
    this.zonaEditada.update(z => ({...z, geolocalizacion: value}));
  }

  updateZonaEditadaBarrio(value: string) {
    // Guardamos el ID del barrio seleccionado
    const barrioId = value ? Number(value) : undefined;
    const barrio = barrioId ? this.barrioService.barrios().find(b => b.id === barrioId) : undefined;
    
    this.zonaEditada.update(z => ({
      ...z, 
      barrio: barrio ? { id: barrio.id!, nombre: barrio.nombre } : undefined
    }));
  }

  eliminarZona(id?: number) {
    if (id === undefined) return;
    
    if (confirm('¿Seguro que deseas eliminar esta zona?')) {
      this.zonaService.eliminarZona(id).subscribe({
        next: () => {
          this._mensajeExito.set('Zona eliminada exitosamente');
        },
        error: (error) => {
          console.error('Error al eliminar zona:', error);
          this._mensajeExito.set('Error al eliminar la zona. Intenta nuevamente.');
        }
      });
    }
  }

  crearZona() {
    const zona = this.nuevaZona();
    
    if (!zona.nombre || !zona.geolocalizacion) {
      this._mensajeExito.set('Completa todos los campos');
      return;
    }
    
    this.zonaService.crearZona(zona).subscribe({
      next: () => {
        this._mensajeExito.set('Zona creada exitosamente');
        
        // Limpiar formulario
        this.nuevaZona.set({
          nombre: '',
          geolocalizacion: '',
          barrio: undefined
        });
        
        // Limpiar búsqueda
        this._search.set('');
      },
      error: (error) => {
        console.error('Error al crear zona:', error);
        this._mensajeExito.set('Error al crear la zona. Intenta nuevamente.');
      }
    });
  }

  cancelarAlta() {
    this._mostrarFormularioAlta.set(false);
    this.nuevaZona.set({
      nombre: '',
      geolocalizacion: '',
      barrio: undefined
    });
  }

  editarZona(zona: Zona) {
    this._zonaEditando.set(zona.id || null);
    
    this.zonaEditada.set({
      id: zona.id,
      nombre: zona.nombre,
      geolocalizacion: zona.geolocalizacion,
      barrio: zona.barrio
    });
  }

  confirmarEdicion() {
    const zona = this.zonaEditada();
    
    if (!zona.nombre || !zona.geolocalizacion) {
      this._mensajeEdicion.set('Completa todos los campos obligatorios');
      return;
    }
    
    const zonaEditandoId = this._zonaEditando();
    if (zonaEditandoId && zona.id) {
      if (confirm('¿Seguro que deseas editar esta zona?')) {
        this.zonaService.actualizarZona(zona.id, zona).subscribe({
          next: (zonaActualizada) => {
            // Actualizar el formulario con los datos actualizados
            this.zonaEditada.set({
              id: zonaActualizada.id,
              nombre: zonaActualizada.nombre,
              geolocalizacion: zonaActualizada.geolocalizacion,
              barrio: zonaActualizada.barrio
            });
            
            this._mensajeEdicion.set('Zona actualizada exitosamente');
          },
          error: (error) => {
            console.error('Error al actualizar zona:', error);
            this._mensajeEdicion.set('Error al actualizar la zona. Intenta nuevamente.');
          }
        });
      }
    }
  }

  cancelarEdicion() {
    this._zonaEditando.set(null);
    this._mensajeEdicion.set('');
    this.zonaEditada.set({
      nombre: '',
      geolocalizacion: '',
      barrio: undefined
    });
  }

  // Métodos para el selector de coordenadas
  abrirSelectorCoordenadas(tipo: 'nueva' | 'editada') {
    this._coordenadasEditando.set(tipo);
    this._mostrarSelectorCoordenadas.set(true);
  }

  cerrarSelectorCoordenadas() {
    this._mostrarSelectorCoordenadas.set(false);
    this._coordenadasEditando.set(null);
  }

  onCoordenadasSeleccionadas(coordenadas: string) {
    const tipoEditando = this._coordenadasEditando();
    
    if (tipoEditando === 'nueva') {
      this.updateNuevaZonaGeolocalizacion(coordenadas);
    } else if (tipoEditando === 'editada') {
      this.updateZonaEditadaGeolocalizacion(coordenadas);
    }
    
    this.cerrarSelectorCoordenadas();
  }

  obtenerCoordenadasActuales(): string {
    const tipoEditando = this._coordenadasEditando();
    
    if (tipoEditando === 'nueva') {
      return this.nuevaZona().geolocalizacion || '';
    } else if (tipoEditando === 'editada') {
      return this.zonaEditada().geolocalizacion || '';
    }
    
    return '';
  }
}
