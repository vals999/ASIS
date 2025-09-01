import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contact.html',
  styleUrls: ['./contact.css']
})
export class ContactComponent {
  // Propiedades del formulario
  contactForm = {
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    mensaje: ''
  };

  constructor() {}

  onSubmit() {
    // Por ahora solo mostramos un console.log, puedes implementar la lógica de envío después
    console.log('Formulario enviado:', this.contactForm);
    
    // Aquí puedes agregar la lógica para enviar el formulario
    // Por ejemplo, llamar a un servicio para enviar el email
    
    // Resetear el formulario después del envío
    this.resetForm();
    
    // Mostrar mensaje de confirmación (puedes usar un toast o modal)
    alert('¡Mensaje enviado correctamente! Te contactaremos pronto.');
  }

  private resetForm() {
    this.contactForm = {
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      mensaje: ''
    };
  }
}