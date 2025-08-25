import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';

interface ArchivoSubido {
  nombre: string;
  tipo: string;
  tamano: number;
  fechaSubida: Date;
  archivo: File;
}

@Component({
  selector: 'app-reportes',
  imports: [CommonModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css'
})
export class ReportesComponent {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  
  archivosSubidos: ArchivoSubido[] = [];

  constructor() { }

  subirReporte(): void {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    const files = target.files;
    
    if (files) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const archivoSubido: ArchivoSubido = {
          nombre: file.name,
          tipo: file.type || 'Desconocido',
          tamano: file.size,
          fechaSubida: new Date(),
          archivo: file
        };
        this.archivosSubidos.push(archivoSubido);
      }
    }
    
    // Limpiar el input para permitir subir el mismo archivo nuevamente si es necesario
    target.value = '';
  }

  formatearTamano(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  eliminarArchivo(index: number): void {
    this.archivosSubidos.splice(index, 1);
  }

  descargarArchivo(archivo: ArchivoSubido): void {
    const url = URL.createObjectURL(archivo.archivo);
    const a = document.createElement('a');
    a.href = url;
    a.download = archivo.nombre;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }
}
