import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DatosPersonalesService, DatosPersonales } from '../../services/datos-personales.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class PerfilComponent implements OnInit {
  // Signals para manejo de estado
  private _datosPersonales = signal<DatosPersonales>({
    nombre: '',
    apellido: '',
    dni: '',
    edad: 0,
    genero: ''
  });

  private _isEditing = signal(false);
  private _mensaje = signal<string | null>(null);
  private _tipoMensaje = signal<'success' | 'error'>('success');

  // Readonly signals para el template
  readonly datosPersonales = this._datosPersonales.asReadonly();
  readonly isEditing = this._isEditing.asReadonly();
  readonly mensaje = this._mensaje.asReadonly();
  readonly tipoMensaje = this._tipoMensaje.asReadonly();

  // Opciones para el dropdown de género
  readonly opcionesGenero = [
    { value: 'Masculino', label: 'Masculino' },
    { value: 'Femenino', label: 'Femenino' },
    { value: 'Otro', label: 'Otro' }
  ];

  constructor(
    public authService: AuthService, // Cambiar a public para que sea accesible en el template
    public datosPersonalesService: DatosPersonalesService, // Cambiar a public para que sea accesible en el template
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarDatosPersonales();
  }

  cargarDatosPersonales() {
    const usuarioActual = this.authService.currentUser();
    if (!usuarioActual) {
      this.router.navigate(['/login']);
      return;
    }

    this.datosPersonalesService.getDatosPersonalesByUsuarioId(usuarioActual.id).subscribe({
      next: (datos) => {
        this._datosPersonales.set(datos);
      },
      error: (error) => {
        console.error('Error al cargar datos personales:', error);
        this._mensaje.set('Error al cargar los datos personales');
        this._tipoMensaje.set('error');
      }
    });
  }

  toggleEdicion() {
    this._isEditing.update(editing => !editing);
    if (!this._isEditing()) {
      // Si cancelamos la edición, volvemos a cargar los datos originales
      this.cargarDatosPersonales();
    }
    this._mensaje.set(null);
  }

  guardarCambios() {
    const usuarioActual = this.authService.currentUser();
    if (!usuarioActual) {
      this.router.navigate(['/login']);
      return;
    }

    const datos = this._datosPersonales();
    
    // Validaciones básicas
    if (!datos.nombre?.trim() || !datos.apellido?.trim() || !datos.dni?.trim()) {
      this._mensaje.set('Por favor complete todos los campos obligatorios');
      this._tipoMensaje.set('error');
      return;
    }

    if (datos.edad < 1 || datos.edad > 120) {
      this._mensaje.set('Por favor ingrese una edad válida');
      this._tipoMensaje.set('error');
      return;
    }

    this.datosPersonalesService.updateDatosPersonalesByUsuarioId(usuarioActual.id, datos).subscribe({
      next: (datosActualizados) => {
        this._datosPersonales.set(datosActualizados);
        this._isEditing.set(false);
        this._mensaje.set('Datos personales actualizados exitosamente');
        this._tipoMensaje.set('success');
        
        // Actualizar los datos del usuario en el AuthService para refrescar el mensaje de bienvenida
        this.authService.updateCurrentUser(datosActualizados.nombre, datosActualizados.apellido);
      },
      error: (error) => {
        console.error('Error al actualizar datos personales:', error);
        this._mensaje.set('Error al actualizar los datos personales. Intente nuevamente.');
        this._tipoMensaje.set('error');
      }
    });
  }

  // Métodos para actualizar los campos del formulario
  updateNombre(value: string) {
    this._datosPersonales.update(datos => ({ ...datos, nombre: value }));
  }

  updateApellido(value: string) {
    this._datosPersonales.update(datos => ({ ...datos, apellido: value }));
  }

  updateDni(value: string) {
    this._datosPersonales.update(datos => ({ ...datos, dni: value }));
  }

  updateEdad(value: string) {
    const edad = parseInt(value) || 0;
    this._datosPersonales.update(datos => ({ ...datos, edad }));
  }

  updateGenero(value: string) {
    this._datosPersonales.update(datos => ({ ...datos, genero: value }));
  }

  limpiarMensaje() {
    this._mensaje.set(null);
    this.datosPersonalesService.clearError();
  }

  goBack() {
    this.router.navigate(['/inicio']);
  }
}