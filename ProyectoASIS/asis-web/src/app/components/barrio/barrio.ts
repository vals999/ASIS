import { Component, OnInit, OnDestroy, signal, computed, effect } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BarrioService, Barrio } from '../../services/barrio.service';
import { CoordenadasSelectorComponent } from '../zona/coordenadas-selector.component';

@Component({
  selector: 'app-barrio',
  standalone: true,
  imports: [CommonModule, FormsModule, CoordenadasSelectorComponent],
  templateUrl: './barrio.html',
  styleUrls: ['./barrio.css']
})
export class BarrioComponent implements OnInit, OnDestroy {
  // Signals locales del componente
  private _search = signal<string>('');
  private _mostrarFormularioAlta = signal<boolean>(false);
  private _mensajeExito = signal<string>('');
  private _mensajeEdicion = signal<string>('');
  private _barrioEditando = signal<number | null>(null);
  private _mostrarSelectorCoordenadas = signal<boolean>(false);
  private _coordenadasEditando = signal<'nuevo' | 'editado' | null>(null);
  
  // Signals públicos de solo lectura
  readonly search = this._search.asReadonly();
  readonly mostrarFormularioAlta = this._mostrarFormularioAlta.asReadonly();
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeEdicion = this._mensajeEdicion.asReadonly();
  readonly barrioEditando = this._barrioEditando.asReadonly();
  readonly mostrarSelectorCoordenadas = this._mostrarSelectorCoordenadas.asReadonly();

  // Signals para formularios
  nuevoBarrio = signal<Barrio>({
    nombre: '',
    geolocalizacion: ''
  });

  barrioEditado = signal<Barrio>({
    nombre: '',
    geolocalizacion: ''
  });

  // Signal computado para filtrado
  readonly barriosFiltrados = computed(() => {
    const barrios = this.barrioService.barrios();
    const searchTerm = this._search().toLowerCase();
    
    if (!searchTerm) return barrios;
    
    return barrios.filter(b =>
      b.nombre.toLowerCase().includes(searchTerm) ||
      b.geolocalizacion.toLowerCase().includes(searchTerm)
    );
  });

  private routerSubscription: Subscription = new Subscription();

  constructor(
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
    this.obtenerBarrios();

    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects.includes('/barrios')) {
        this.obtenerBarrios();
      }
    });
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }

  obtenerBarrios() {
    this.barrioService.getBarrios().subscribe();
  }

  // Métodos para actualizar signals - NUEVO BARRIO
  updateSearch(value: string) {
    this._search.set(value);
  }

  toggleFormularioAlta() {
    this._mostrarFormularioAlta.update(show => !show);
  }

  updateNuevoBarrioNombre(value: string) {
    this.nuevoBarrio.update(b => ({...b, nombre: value}));
  }

  updateNuevoBarrioGeolocalizacion(value: string) {
    this.nuevoBarrio.update(b => ({...b, geolocalizacion: value}));
  }

  // Métodos para actualizar signals - BARRIO EDITADO
  updateBarrioEditadoNombre(value: string) {
    this.barrioEditado.update(b => ({...b, nombre: value}));
  }

  updateBarrioEditadoGeolocalizacion(value: string) {
    this.barrioEditado.update(b => ({...b, geolocalizacion: value}));
  }

  eliminarBarrio(id?: number) {
    if (id === undefined) return;
    
    if (confirm('¿Seguro que deseas eliminar este barrio?')) {
      this.barrioService.eliminarBarrio(id).subscribe({
        next: () => {
          this._mensajeExito.set('Barrio eliminado exitosamente');
        },
        error: (error) => {
          console.error('Error al eliminar barrio:', error);
          this._mensajeExito.set('Error al eliminar el barrio. Intenta nuevamente.');
        }
      });
    }
  }

  crearBarrio() {
    const barrio = this.nuevoBarrio();
    
    if (!barrio.nombre || !barrio.geolocalizacion) {
      this._mensajeExito.set('Completa todos los campos');
      return;
    }
    
    this.barrioService.crearBarrio(barrio).subscribe({
      next: () => {
        this._mensajeExito.set('Barrio creado exitosamente');
        
        // Limpiar formulario
        this.nuevoBarrio.set({
          nombre: '',
          geolocalizacion: ''
        });
        
        // Limpiar búsqueda
        this._search.set('');
      },
      error: (error) => {
        console.error('Error al crear barrio:', error);
        this._mensajeExito.set('Error al crear el barrio. Intenta nuevamente.');
      }
    });
  }

  cancelarAlta() {
    this._mostrarFormularioAlta.set(false);
    this.nuevoBarrio.set({
      nombre: '',
      geolocalizacion: ''
    });
  }

  editarBarrio(barrio: Barrio) {
    this._barrioEditando.set(barrio.id || null);
    
    this.barrioEditado.set({
      id: barrio.id,
      nombre: barrio.nombre,
      geolocalizacion: barrio.geolocalizacion
    });
  }

  confirmarEdicion() {
    const barrio = this.barrioEditado();
    
    if (!barrio.nombre || !barrio.geolocalizacion) {
      this._mensajeEdicion.set('Completa todos los campos obligatorios');
      return;
    }
    
    const barrioEditandoId = this._barrioEditando();
    if (barrioEditandoId && barrio.id) {
      if (confirm('¿Seguro que deseas editar este barrio?')) {
        this.barrioService.actualizarBarrio(barrio.id, barrio).subscribe({
          next: (barrioActualizado) => {
            // Actualizar el formulario con los datos actualizados
            this.barrioEditado.set({
              id: barrioActualizado.id,
              nombre: barrioActualizado.nombre,
              geolocalizacion: barrioActualizado.geolocalizacion
            });
            
            this._mensajeEdicion.set('Barrio actualizado exitosamente');
          },
          error: (error) => {
            console.error('Error al actualizar barrio:', error);
            this._mensajeEdicion.set('Error al actualizar el barrio. Intenta nuevamente.');
          }
        });
      }
    }
  }

  cancelarEdicion() {
    this._barrioEditando.set(null);
    this._mensajeEdicion.set('');
    this.barrioEditado.set({
      nombre: '',
      geolocalizacion: ''
    });
  }

  // Métodos para el selector de coordenadas
  abrirSelectorCoordenadas(tipo: 'nuevo' | 'editado') {
    this._coordenadasEditando.set(tipo);
    this._mostrarSelectorCoordenadas.set(true);
  }

  cerrarSelectorCoordenadas() {
    this._mostrarSelectorCoordenadas.set(false);
    this._coordenadasEditando.set(null);
  }

  onCoordenadasSeleccionadas(coordenadas: string) {
    const tipoEditando = this._coordenadasEditando();
    
    if (tipoEditando === 'nuevo') {
      this.updateNuevoBarrioGeolocalizacion(coordenadas);
    } else if (tipoEditando === 'editado') {
      this.updateBarrioEditadoGeolocalizacion(coordenadas);
    }
    
    this.cerrarSelectorCoordenadas();
  }

  obtenerCoordenadasActuales(): string {
    const tipoEditando = this._coordenadasEditando();
    
    if (tipoEditando === 'nuevo') {
      return this.nuevoBarrio().geolocalizacion || '';
    } else if (tipoEditando === 'editado') {
      return this.barrioEditado().geolocalizacion || '';
    }
    
    return '';
  }
}
