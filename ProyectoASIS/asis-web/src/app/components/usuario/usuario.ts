import { Component, OnInit, OnDestroy, signal, computed, effect } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuariosService, Usuario } from '../../services/usuario.service';

/*Signals son contenedores de valores que notifican automáticamente cuando cambian, 
permitiendo que Angular actualice la UI de manera granular y optimizada.
Angular sabe exactamente qué cambió y solo actualiza esas partes*/

@Component({
  selector: 'app-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuario.html',
  styleUrls: ['./usuario.css']
})
export class UsuarioComponent implements OnInit, OnDestroy {
  // Signals locales del componente. estado interno del componente encapsulado
  private _search = signal<string>('');
  private _mostrarFormularioAlta = signal<boolean>(false);
  private _mensajeExito = signal<string>('');
  private _mensajeEdicion = signal<string>('');
  private _usuarioEditando = signal<number | null>(null);
  private _paginaActual = signal<number>(1);
  private _elementosPorPagina = signal<number>(10);
  
  // Signals públicos de solo lectura. el template accede a los valores sin poder modificarlos
  readonly search = this._search.asReadonly();
  readonly mostrarFormularioAlta = this._mostrarFormularioAlta.asReadonly();
  readonly mensajeExito = this._mensajeExito.asReadonly();
  readonly mensajeEdicion = this._mensajeEdicion.asReadonly();
  readonly usuarioEditando = this._usuarioEditando.asReadonly();
  readonly paginaActual = this._paginaActual.asReadonly();
  readonly elementosPorPagina = this._elementosPorPagina.asReadonly();

  // Signals para formularios. Mantienen el estado de los formularios
  nuevoUsuario = signal<Usuario>({
    nombreUsuario: '',
    email: '',
    contrasena: '',
    perfil: '',
    habilitado: true
  });

  usuarioEditado = signal<Usuario>({
    nombreUsuario: '',
    email: '',
    perfil: '',
    habilitado: true
  });

  /*Signal computado para filtrado. Crea un signal que se calcula automáticamente basado en otros signals
    Se re-ejecuta SOLO cuando sus dependencias cambian. 
    Filtra la lista de usuarios automáticamente cuando cambia _search o usuarios() del servicio. */
  readonly usuariosFiltrados = computed(() => {
    const usuarios = this.usuariosService.usuarios();
    const searchTerm = this._search().toLowerCase();
    
    // Filtrar solo usuarios habilitados
    const usuariosHabilitados = usuarios.filter(u => u.habilitado);
    
    if (!searchTerm) return usuariosHabilitados;
    
    return usuariosHabilitados.filter(u =>
      u.nombreUsuario.toLowerCase().includes(searchTerm) ||
      u.email.toLowerCase().includes(searchTerm) ||
      u.perfil.toLowerCase().includes(searchTerm)
    );
  });

  // Signals computados para paginación
  readonly totalPaginas = computed(() => {
    const totalUsuarios = this.usuariosFiltrados().length;
    return Math.ceil(totalUsuarios / this._elementosPorPagina());
  });

  readonly usuariosPaginados = computed(() => {
    const usuarios = this.usuariosFiltrados();
    const inicio = (this._paginaActual() - 1) * this._elementosPorPagina();
    const fin = inicio + this._elementosPorPagina();
    return usuarios.slice(inicio, fin);
  });

  readonly paginasArray = computed(() => {
    const total = this.totalPaginas();
    return Array.from({ length: total }, (_, i) => i + 1);
  });

  // Opciones del dropdown para perfil
  readonly opcionesPerfiles = [
    { value: 'ADMINISTRADOR', label: 'Administrador' },
    { value: 'PERSONAL_SALUD', label: 'Personal de Salud' },
    { value: 'REFERENTE_ORG_SOCIAL', label: 'Referente Organización Social' }
  ];

  private routerSubscription: Subscription = new Subscription();

  constructor(
    public usuariosService: UsuariosService,
    private router: Router
  ) {
    /*Effect para auto-limpiar mensajes después de 3 segundos
	Se ejecuta automáticamente cuando cualquiera de sus signals dependientes cambia. para efectos secundarios
	*/
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
  // Inicializa los datos (carga usuarios)
  ngOnInit() {
    this.obtenerUsuarios();

    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects.includes('/usuarios')) {
        this.obtenerUsuarios();
      }
    });
  }
  // Limpia subscripciones manuales
  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
  }

  obtenerUsuarios() {
    this.usuariosService.getUsuarios().subscribe();
  }

  // Métodos para actualizar signals - NUEVO USUARIO
  updateSearch(value: string) {
    this._search.set(value);
    // Resetear a la primera página cuando se busca
    this._paginaActual.set(1);
  }

  toggleFormularioAlta() {
    this._mostrarFormularioAlta.update(show => !show);
  }

  updateNuevoUsuarioNombre(value: string) {
    this.nuevoUsuario.update(u => ({...u, nombreUsuario: value}));
  }

  updateNuevoUsuarioEmail(value: string) {
    this.nuevoUsuario.update(u => ({...u, email: value}));
  }

  updateNuevoUsuarioContrasena(value: string) {
    this.nuevoUsuario.update(u => ({...u, contrasena: value}));
  }

  updateNuevoUsuarioPerfil(value: string) {
    this.nuevoUsuario.update(u => ({...u, perfil: value}));
  }

  updateNuevoUsuarioHabilitado(value: string) {
    this.nuevoUsuario.update(u => ({...u, habilitado: value === 'true'}));
  }

  // Métodos para actualizar signals - USUARIO EDITADO
  updateUsuarioEditadoNombre(value: string) {
    this.usuarioEditado.update(u => ({...u, nombreUsuario: value}));
  }

  updateUsuarioEditadoEmail(value: string) {
    this.usuarioEditado.update(u => ({...u, email: value}));
  }

  updateUsuarioEditadoPerfil(value: string) {
    this.usuarioEditado.update(u => ({...u, perfil: value}));
  }

  updateUsuarioEditadoHabilitado(value: string) {
    this.usuarioEditado.update(u => ({...u, habilitado: value === 'true'}));
  }

  eliminarUsuario(id?: number) {
    if (id === undefined) return;
    
    if (confirm('¿Seguro que deseas eliminar este usuario?')) {
      this.usuariosService.eliminarUsuario(id).subscribe({
        next: () => {
          this._mensajeExito.set('Usuario eliminado exitosamente');
        },
        error: (error) => {
          console.error('Error al eliminar usuario:', error);
          this._mensajeExito.set('Error al eliminar el usuario. Intenta nuevamente.');
        }
      });
    }
  }

  crearUsuario() {
    const usuario = this.nuevoUsuario();
    
    if (!usuario.nombreUsuario || !usuario.email || !usuario.contrasena || !usuario.perfil) {
      this._mensajeExito.set('Completa todos los campos');
      return;
    }
    
    this.usuariosService.crearUsuario(usuario).subscribe({
      next: () => {
        this._mensajeExito.set('Usuario creado exitosamente');
        
        // Limpiar formulario
        this.nuevoUsuario.set({
          nombreUsuario: '',
          email: '',
          contrasena: '',
          perfil: '',
          habilitado: true
        });
        
        // Limpiar búsqueda
        this._search.set('');
      },
      error: (error) => {
        console.error('Error al crear usuario:', error);
        this._mensajeExito.set('Error al crear el usuario. Intenta nuevamente.');
      }
    });
  }

  cancelarAlta() {
    this._mostrarFormularioAlta.set(false);
    this.nuevoUsuario.set({
      nombreUsuario: '',
      email: '',
      contrasena: '',
      perfil: '',
      habilitado: true
    });
  }

  editarUsuario(usuario: Usuario) {
    this._usuarioEditando.set(usuario.id || null);
    
    this.usuarioEditado.set({
      id: usuario.id,
      nombreUsuario: usuario.nombreUsuario,
      email: usuario.email,
      perfil: usuario.perfil,
      habilitado: usuario.habilitado
    });
  }

  confirmarEdicion() {
    const usuario = this.usuarioEditado();
    
    if (!usuario.nombreUsuario || !usuario.email || !usuario.perfil) {
      this._mensajeEdicion.set('Completa todos los campos obligatorios');
      return;
    }
    
    const usuarioEditandoId = this._usuarioEditando();
    if (usuarioEditandoId && usuario.id) {
      if (confirm('¿Seguro que deseas editar este usuario?')) {
        this.usuariosService.actualizarUsuario(usuario.id, usuario).subscribe({
          next: (usuarioActualizado) => {
            // Actualizar el formulario con los datos actualizados
            this.usuarioEditado.set({
              id: usuarioActualizado.id,
              nombreUsuario: usuarioActualizado.nombreUsuario,
              email: usuarioActualizado.email,
              perfil: usuarioActualizado.perfil,
              habilitado: usuarioActualizado.habilitado
            });
            
            this._mensajeEdicion.set('Usuario actualizado exitosamente');
          },
          error: (error) => {
            console.error('Error al actualizar usuario:', error);
            this._mensajeEdicion.set('Error al actualizar el usuario. Intenta nuevamente.');
          }
        });
      }
    }
  }

  cancelarEdicion() {
    this._usuarioEditando.set(null);
    this._mensajeEdicion.set('');
    this.usuarioEditado.set({
      nombreUsuario: '',
      email: '',
      perfil: '',
      habilitado: true
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
