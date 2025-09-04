  import { BaseChartDirective } from 'ng2-charts';
  import { Component } from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { FormsModule } from '@angular/forms';
  import { EstadisticasService, PreguntaRespuestaCategoria, Filtros } from '../../services/estadisticas.service';
  import { ChangeDetectorRef } from '@angular/core';

  @Component({
    selector: 'app-estadisticas',
    templateUrl: './estadisticas.html',
    styleUrls: ['./estadisticas.css'],
    standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective]
  })
  export class EstadisticasComponent {
 
    zonas: string[] = [];
    barrios: string[] = [];
    campanias: string[] = [];
    sexos: string[] = [];
    edades: number[] = [];
    edadRango: number[] = Array.from({length: 101}, (_, i) => i);
    preguntas: string[] = [];
    preguntaSeleccionada: string = '';
    preguntasRespuestas: PreguntaRespuestaCategoria[] = [];
    filtros: Filtros = {};
    categorias: string[] = [];
     // ...existing properties...
    barChartData: { datasets: { data: number[]; label: string }[]; labels: string[] } = { datasets: [{ data: [], label: 'Cantidad' }], labels: [] };
    barChartOptions = {
      responsive: true,
  indexAxis: 'y' as const, // barras verticales, etiquetas debajo de cada barra
      plugins: {
        legend: { display: false },
        title: { display: true, text: 'Respuestas por opción' }
      },
      scales: {
        x: {
          title: { display: true, text: 'Tipo de respuesta' },
          ticks: { autoSkip: false, maxRotation: 0, minRotation: 0 }
        },
        y: {
          title: { display: true, text: 'Cantidad' }
        }
      }
    };

  constructor(private estadisticasService: EstadisticasService, private cdr: ChangeDetectorRef) {}

    ngOnInit() {
      this.cargarTodasLasCategorias();
      this.cargarTodasLasPreguntas();
    }

    aplicarFiltros(): void {
      // Actualizar los filtros antes de cargar los datos
      if (!this.filtros.categoria) {
        delete this.filtros.categoria;
      }
      if (!this.preguntaSeleccionada) {
        delete (this.filtros as any).pregunta;
      } else {
        (this.filtros as any).pregunta = this.preguntaSeleccionada;
      }
      // Edad: si no hay valor, eliminar del filtro
      if (!this.filtros.edadDesde) {
        delete this.filtros.edadDesde;
      }
      if (!this.filtros.edadHasta) {
        delete this.filtros.edadHasta;
      }
      // Ahora sí cargar los datos y el gráfico
      this.cargarPreguntasRespuestas();
    }

    onCategoriaChange() {
      if (!this.filtros.categoria) {
        // Si selecciona 'Todas', eliminar el filtro
        delete this.filtros.categoria;
        this.cargarTodasLasPreguntas();
      } else {
        // Si hay categoría, filtrar solo preguntas por esa categoría
        this.estadisticasService.filtrarPreguntasRespuestas({ categoria: this.filtros.categoria }).subscribe(data => {
          this.preguntas = Array.from(new Set(data.map(pr => pr.pregunta).filter(Boolean)));
        });
      }
    }

    onPreguntaSeleccionada(pregunta: string) {
      this.preguntaSeleccionada = pregunta;
      if (!pregunta) {
        // Si selecciona 'Todas', eliminar el filtro
        delete (this.filtros as any).pregunta;
      } else {
        (this.filtros as any).pregunta = pregunta;
      }
    }

    cargarTodasLasPreguntas() {
      this.estadisticasService.filtrarPreguntasRespuestas({}).subscribe(data => {
        this.preguntas = Array.from(new Set(data.map(pr => pr.pregunta).filter(Boolean)));
      });
    }

    cargarTodasLasCategorias() {
      this.estadisticasService.filtrarPreguntasRespuestas({}).subscribe(data => {
        this.categorias = Array.from(new Set(data.map(pr => pr.categoria).filter(Boolean)));
      });
    }

    cargarPreguntasRespuestas() {
      // ...existing code...
      // Si existe 'campaña', renombrar a 'campania' antes de enviar
      if ((this.filtros as any)['campaña']) {
        (this.filtros as any)['campania'] = (this.filtros as any)['campaña'];
        delete (this.filtros as any)['campaña'];
      }
      this.estadisticasService.filtrarPreguntasRespuestas(this.filtros).subscribe(data => {
        this.preguntasRespuestas = data;
        // NO actualizar categorias aquí, solo en cargarTodasLasCategorias()
        this.zonas = Array.from(new Set(data.map((pr: any) => pr.zona).filter(Boolean)));
        this.barrios = Array.from(new Set(data.map((pr: any) => pr.barrio).filter(Boolean)));
        this.campanias = Array.from(new Set(data.map((pr: any) => pr.campania).filter(Boolean)));
        this.sexos = Array.from(new Set(data.map((pr: any) => pr.sexo).filter(Boolean)));
        // NO actualizar preguntas aquí, solo en cargarTodasLasPreguntas()
        const textoPreguntaEdad = '¿Qué edad tenés?';
        this.edades = Array.from(new Set(
          data
            .filter((pr: any) => pr.pregunta && pr.pregunta.toLowerCase().includes('edad'))
            .map((pr: any) => {
              const valor = pr.respuesta;
              const num = Number(valor);
              return isNaN(num) ? null : num;
            })
        )).filter((e: any): e is number => typeof e === 'number' && !isNaN(e));

        this.actualizarDatosGrafico();
        this.cdr.detectChanges();
      });
    }

    actualizarDatosGrafico() {
      // Agrupar respuestas por opción (para la pregunta seleccionada)
      // Si hay pregunta seleccionada, agrupar solo las respuestas de esa pregunta
      let datosParaGraficar = this.preguntasRespuestas;
      if (this.preguntaSeleccionada) {
        datosParaGraficar = this.preguntasRespuestas.filter(pr => pr.pregunta === this.preguntaSeleccionada);
      }
      // Agrupar por tipo de respuesta (ej: 'Sí', 'No', etc.)
      const conteo: { [tipo: string]: number } = {};
      datosParaGraficar.forEach(pr => {
        const tipo = pr.respuesta ? pr.respuesta.toString() : '-';
        conteo[tipo] = (conteo[tipo] || 0) + 1;
      });
      this.barChartData = {
        labels: Object.keys(conteo),
        datasets: [{ data: Object.values(conteo), label: 'Cantidad' }]
      };
    }

    onFiltroChange() {
  // Solo actualiza el filtro en memoria, no carga datos
    }
  }
