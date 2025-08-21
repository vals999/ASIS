import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css'
})
export class Inicio {
  selectedFile: File | null = null;
  uploadMessage: string = '';

  constructor(
    public authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {}

  get perfil(): string | undefined {
    return this.authService.currentUser()?.perfil;
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }
  
  onCsvUpload(event: Event) {
    event.preventDefault();
    if (!this.selectedFile) return;
    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post('http://localhost:8080/ProyectoASIS/api/import-csv', formData).subscribe({
      next: () => this.uploadMessage = 'Archivo importado correctamente.',
      error: () => this.uploadMessage = 'Error al importar el archivo.'
    });
  }

  navigateToMap(): void {
    this.router.navigate(['/mapa']);
  }
}