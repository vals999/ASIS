import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuariosService, Usuario } from '../../services/usuario.service';

@Component({
  selector: 'app-usuarios-pendientes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './usuarios-pendientes.html',
  styleUrls: ['./usuarios-pendientes.css']
})
export class UsuariosPendientesComponent implements OnInit {
  
  confirmationUserId = signal<number | null>(null);
  private _paginaActual = signal<number>(1);
  private _elementosPorPagina = signal<number>(10);
  
  readonly paginaActual = this._paginaActual.asReadonly();
  readonly elementosPorPagina = this._elementosPorPagina.asReadonly();

  // Signals computados para paginación
  readonly totalPaginas = computed(() => {
    const totalUsuarios = this.usuariosService.usuariosPendientes().length;
    return Math.ceil(totalUsuarios / this._elementosPorPagina());
  });

  readonly usuariosPaginados = computed(() => {
    const usuarios = this.usuariosService.usuariosPendientes();
    const inicio = (this._paginaActual() - 1) * this._elementosPorPagina();
    const fin = inicio + this._elementosPorPagina();
    return usuarios.slice(inicio, fin);
  });

  readonly paginasArray = computed(() => {
    const total = this.totalPaginas();
    return Array.from({ length: total }, (_, i) => i + 1);
  });

  constructor(public usuariosService: UsuariosService) {}

  ngOnInit(): void {
    this.cargarUsuariosPendientes();
  }

  cargarUsuariosPendientes(): void {
    this.usuariosService.getUsuariosPendientes().subscribe({
      next: () => {
        console.log('Usuarios pendientes cargados');
      },
      error: (error) => {
        console.error('Error al cargar usuarios pendientes:', error);
      }
    });
  }

  mostrarConfirmacion(userId: number): void {
    this.confirmationUserId.set(userId);
  }

  cancelarConfirmacion(): void {
    this.confirmationUserId.set(null);
  }

  confirmarHabilitacion(userId: number): void {
    this.usuariosService.habilitarUsuario(userId).subscribe({
      next: () => {
        console.log('Usuario habilitado exitosamente');
        this.confirmationUserId.set(null);
        // Opcional: mostrar mensaje de éxito
      },
      error: (error) => {
        console.error('Error al habilitar usuario:', error);
        this.confirmationUserId.set(null);
      }
    });
  }

  rechazarUsuario(userId: number): void {
    if (confirm('¿Está seguro que desea rechazar este usuario? Esta acción eliminará el usuario del sistema.')) {
      this.usuariosService.eliminarUsuario(userId).subscribe({
        next: () => {
          console.log('Usuario rechazado exitosamente');
          // El servicio ya actualiza automáticamente la lista de usuarios pendientes
        },
        error: (error) => {
          console.error('Error al rechazar usuario:', error);
        }
      });
    }
  }

  recargar(): void {
    this.cargarUsuariosPendientes();
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
