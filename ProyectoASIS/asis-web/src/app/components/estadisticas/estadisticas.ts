import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EstadisticasService, PreguntaRespuesta } from '../../services/estadisticas.service';

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './estadisticas.html',
})
export class EstadisticasComponent implements OnInit {
  private _datos: PreguntaRespuesta[] = [];
  get datos() {
    return this._datos;
  }
  set datos(val: PreguntaRespuesta[]) {
    this._datos = val;
  }
  categoriaSeleccionada: string = '';
  categorias: string[] = [];
  loading = true;
  error = '';

  // Paginación
  paginaActual = 1;
  preguntasPorPagina = 20;

  constructor(private estadisticasService: EstadisticasService) {}

  ngOnInit() {
    this.estadisticasService.getPreguntasRespuestas().subscribe({
      next: (datos) => {
        this.datos = datos;
        this.categorias = Array.from(new Set(datos.map(d => d.categoria))).filter(c => c);
        this.loading = false;
      },
      error: () => {
        this.error = 'Error cargando datos';
        this.loading = false;
      }
    });
  }

  get datosFiltrados() {
    if (!this.categoriaSeleccionada) return this.datos;
    return this.datos.filter(d => d.categoria === this.categoriaSeleccionada);
  }

  agruparPorPregunta(datos: PreguntaRespuesta[]) {
    const mapa = new Map<string, { pregunta: string, respuestas: string[], categoria: string }>();
    datos.forEach(d => {
      if (!mapa.has(d.pregunta)) {
        mapa.set(d.pregunta, { pregunta: d.pregunta, respuestas: [d.respuesta], categoria: d.categoria });
      } else {
        mapa.get(d.pregunta)!.respuestas.push(d.respuesta);
      }
    });
    return Array.from(mapa.values());
  }

  get preguntasPaginadas() {
    const agrupadas = this.agruparPorPregunta(this.datosFiltrados);
    const inicio = (this.paginaActual - 1) * this.preguntasPorPagina;
    return agrupadas.slice(inicio, inicio + this.preguntasPorPagina);
  }

  get totalPaginas() {
    return Math.ceil(this.agruparPorPregunta(this.datosFiltrados).length / this.preguntasPorPagina);
  }

  cambiarPagina(delta: number) {
    const nueva = this.paginaActual + delta;
    if (nueva >= 1 && nueva <= this.totalPaginas) {
      this.paginaActual = nueva;
    }
  }

  irAPagina(p: number) {
    if (p >= 1 && p <= this.totalPaginas) {
      this.paginaActual = p;
    }
  }
}
