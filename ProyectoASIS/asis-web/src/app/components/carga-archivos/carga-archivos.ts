import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-carga-archivos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './carga-archivos.html',
  styleUrls: ['./carga-archivos.css']
})
export class CargaArchivosComponent {
  selectedFile = signal<File | null>(null);
  uploadMessage = signal<string>('');
  uploading = signal<boolean>(false);

  constructor(
    public authService: AuthService,
    private http: HttpClient
  ) {}

  get perfil(): string | undefined {
    return this.authService.currentUser()?.perfil;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    this.selectedFile.set(file || null);
    
    // Limpiar mensaje anterior
    this.uploadMessage.set('');
  }

  onCsvUpload(event: Event) {
    event.preventDefault();
    
    const file = this.selectedFile();
    if (!file) {
      this.uploadMessage.set('Error: No se ha seleccionado ningún archivo');
      return;
    }

    // Validar que sea un archivo CSV
    if (!file.name.toLowerCase().endsWith('.csv')) {
      this.uploadMessage.set('Error: El archivo debe tener extensión .csv');
      return;
    }

    // Validar tamaño del archivo (máximo 10MB)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      this.uploadMessage.set('Error: El archivo es demasiado grande. Máximo 10MB.');
      return;
    }

    this.uploading.set(true);
    this.uploadMessage.set('');

    const formData = new FormData();
    formData.append('file', file);

    this.http.post('http://localhost:8080/ProyectoASIS/api/import-csv', formData).subscribe({
      next: (response) => {
        this.uploading.set(false);
        this.uploadMessage.set(`Archivo "${file.name}" importado correctamente.`);
        
        // Limpiar el archivo seleccionado
        this.selectedFile.set(null);
        
        // Limpiar el input file
        const fileInput = document.getElementById('csv-file') as HTMLInputElement;
        if (fileInput) {
          fileInput.value = '';
        }

        // Auto-limpiar mensaje después de 5 segundos
        setTimeout(() => {
          this.uploadMessage.set('');
        }, 5000);
      },
      error: (error) => {
        this.uploading.set(false);
        console.error('Error al importar archivo CSV:', error);
        
        let errorMessage = 'Error desconocido al importar el archivo.';
        
        if (error.status === 400) {
          errorMessage = 'Error: Formato de archivo inválido o datos incorrectos.';
        } else if (error.status === 500) {
          errorMessage = 'Error del servidor al procesar el archivo.';
        } else if (error.error && typeof error.error === 'string') {
          errorMessage = `Error: ${error.error}`;
        }
        
        this.uploadMessage.set(errorMessage);
      }
    });
  }
}