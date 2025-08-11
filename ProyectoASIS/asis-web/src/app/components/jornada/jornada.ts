import { Component, OnInit, OnDestroy, signal, computed, effect } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JornadaService, Jornada } from '../../services/jornada.service';

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
  
  // Signals públicos de solo lectura
  readonly search = this._search.asReadonly();
  readonly mostrarFormularioAlta = this._mostrarFormularioAlta.asReadonly();
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeEdicion = this._mensajeEdicion.asReadonly();
  readonly jornadaEditando = this._jornadaEditando.asReadonly();

  // Signals para formularios
  nuevaJornada = signal<Jornada>({
    fecha: ''
  });

  jornadaEditada = signal<Jornada>({
    fecha: ''
  });

  // Signal computado para filtrado
  readonly jornadasFiltradas = computed(() => {
    const jornadas = this.jornadaService.jornadas();
    const searchTerm = this._search().toLowerCase();
    
    if (!searchTerm) return jornadas;
    
    return jornadas.filter(j =>
      j.fecha.includes(searchTerm)
    );
  });

  private routerSubscription: Subscription = new Subscription();

  constructor(
    public jornadaService: JornadaService,
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

  // Métodos para actualizar signals - NUEVA JORNADA
  updateSearch(value: string) {
    this._search.set(value);
  }

  toggleFormularioAlta() {
    this._mostrarFormularioAlta.update(show => !show);
  }

  updateNuevaJornadaFecha(value: string) {
    this.nuevaJornada.update(j => ({...j, fecha: value}));
  }

  // Métodos para actualizar signals - JORNADA EDITADA
  updateJornadaEditadaFecha(value: string) {
    this.jornadaEditada.update(j => ({...j, fecha: value}));
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
      next: () => {
        this._mensajeExito.set('Jornada creada exitosamente');
        
        // Limpiar formulario
        this.nuevaJornada.set({
          fecha: ''
        });
        
        // Limpiar búsqueda
        this._search.set('');
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
      fecha: ''
    });
  }

  editarJornada(jornada: Jornada) {
    this._jornadaEditando.set(jornada.id || null);
    
    this.jornadaEditada.set({
      id: jornada.id,
      fecha: jornada.fecha
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
              fecha: jornadaActualizada.fecha
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
      fecha: ''
    });
  }
}
