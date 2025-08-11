import { Component, OnInit, OnDestroy, signal, computed, effect } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EncuestadorService, Encuestador } from '../../services/encuestador.service';

@Component({
  selector: 'app-encuestador',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './encuestador.html',
  styleUrls: ['./encuestador.css']
})
export class EncuestadorComponent implements OnInit, OnDestroy {
  // Signals locales del componente
  private _search = signal<string>('');
  private _mostrarFormularioAlta = signal<boolean>(false);
  private _mensajeExito = signal<string>('');
  private _mensajeEdicion = signal<string>('');
  private _encuestadorEditando = signal<number | null>(null);
  
  // Signals públicos de solo lectura
  readonly search = this._search.asReadonly();
  readonly mostrarFormularioAlta = this._mostrarFormularioAlta.asReadonly();
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeEdicion = this._mensajeEdicion.asReadonly();
  readonly encuestadorEditando = this._encuestadorEditando.asReadonly();

  // Signals para formularios
  nuevoEncuestador = signal<Encuestador>({
    ocupacion: ''
  });

  encuestadorEditado = signal<Encuestador>({
    ocupacion: ''
  });

  // Signal computado para filtrado
  readonly encuestadoresFiltrados = computed(() => {
    const encuestadores = this.encuestadorService.encuestadores();
    const searchTerm = this._search().toLowerCase();
    
    if (!searchTerm) return encuestadores;
    
    return encuestadores.filter(e =>
      e.ocupacion.toLowerCase().includes(searchTerm) ||
      (e.datosPersonales?.nombre && e.datosPersonales.nombre.toLowerCase().includes(searchTerm)) ||
      (e.datosPersonales?.apellido && e.datosPersonales.apellido.toLowerCase().includes(searchTerm)) ||
      (e.datosPersonales?.dni && e.datosPersonales.dni.includes(searchTerm)) ||
      (e.usuario?.nombreUsuario && e.usuario.nombreUsuario.toLowerCase().includes(searchTerm))
    );
  });

  private routerSubscription: Subscription = new Subscription();

  constructor(
    public encuestadorService: EncuestadorService,
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
    this.obtenerEncuestadores();

    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects.includes('/encuestadores')) {
        this.obtenerEncuestadores();
      }
    });
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }

  obtenerEncuestadores() {
    this.encuestadorService.getEncuestadores().subscribe();
  }

  // Métodos para actualizar signals - NUEVO ENCUESTADOR
  updateSearch(value: string) {
    this._search.set(value);
  }

  toggleFormularioAlta() {
    this._mostrarFormularioAlta.update(show => !show);
  }

  updateNuevoEncuestadorOcupacion(value: string) {
    this.nuevoEncuestador.update(e => ({...e, ocupacion: value}));
  }

  // Métodos para actualizar signals - ENCUESTADOR EDITADO
  updateEncuestadorEditadoOcupacion(value: string) {
    this.encuestadorEditado.update(e => ({...e, ocupacion: value}));
  }

  eliminarEncuestador(id?: number) {
    if (id === undefined) return;
    
    if (confirm('¿Seguro que deseas eliminar este encuestador?')) {
      this.encuestadorService.eliminarEncuestador(id).subscribe({
        next: () => {
          this._mensajeExito.set('Encuestador eliminado exitosamente');
        },
        error: (error) => {
          console.error('Error al eliminar encuestador:', error);
          this._mensajeExito.set('Error al eliminar el encuestador. Intenta nuevamente.');
        }
      });
    }
  }

  crearEncuestador() {
    const encuestador = this.nuevoEncuestador();
    
    if (!encuestador.ocupacion) {
      this._mensajeExito.set('Completa todos los campos');
      return;
    }
    
    this.encuestadorService.crearEncuestador(encuestador).subscribe({
      next: () => {
        this._mensajeExito.set('Encuestador creado exitosamente');
        
        // Limpiar formulario
        this.nuevoEncuestador.set({
          ocupacion: ''
        });
        
        // Limpiar búsqueda
        this._search.set('');
      },
      error: (error) => {
        console.error('Error al crear encuestador:', error);
        this._mensajeExito.set('Error al crear el encuestador. Intenta nuevamente.');
      }
    });
  }

  cancelarAlta() {
    this._mostrarFormularioAlta.set(false);
    this.nuevoEncuestador.set({
      ocupacion: ''
    });
  }

  editarEncuestador(encuestador: Encuestador) {
    this._encuestadorEditando.set(encuestador.id || null);
    
    this.encuestadorEditado.set({
      id: encuestador.id,
      ocupacion: encuestador.ocupacion,
      datosPersonales: encuestador.datosPersonales,
      usuario: encuestador.usuario
    });
  }

  confirmarEdicion() {
    const encuestador = this.encuestadorEditado();
    
    if (!encuestador.ocupacion) {
      this._mensajeEdicion.set('Completa todos los campos obligatorios');
      return;
    }
    
    const encuestadorEditandoId = this._encuestadorEditando();
    if (encuestadorEditandoId && encuestador.id) {
      if (confirm('¿Seguro que deseas editar este encuestador?')) {
        this.encuestadorService.actualizarEncuestador(encuestador.id, encuestador).subscribe({
          next: (encuestadorActualizado) => {
            // Actualizar el formulario con los datos actualizados
            this.encuestadorEditado.set({
              id: encuestadorActualizado.id,
              ocupacion: encuestadorActualizado.ocupacion,
              datosPersonales: encuestadorActualizado.datosPersonales,
              usuario: encuestadorActualizado.usuario
            });
            
            this._mensajeEdicion.set('Encuestador actualizado exitosamente');
          },
          error: (error) => {
            console.error('Error al actualizar encuestador:', error);
            this._mensajeEdicion.set('Error al actualizar el encuestador. Intenta nuevamente.');
          }
        });
      }
    }
  }

  cancelarEdicion() {
    this._encuestadorEditando.set(null);
    this._mensajeEdicion.set('');
    this.encuestadorEditado.set({
      ocupacion: ''
    });
  }
}