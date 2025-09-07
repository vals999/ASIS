import { Component, OnInit, signal } from '@angular/core';
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
}
