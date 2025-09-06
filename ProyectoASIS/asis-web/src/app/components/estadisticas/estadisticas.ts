import { BaseChartDirective } from 'ng2-charts';
import { Component, OnInit, AfterViewInit } from '@angular/core';
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
export class EstadisticasComponent implements OnInit, AfterViewInit {

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

  // Estadísticas resumidas
  estadisticasResumen = {
    totalRespuestas: 0,
    respuestaMasComun: '',
    cantidadRespuestaMasComun: 0,
    respuestasUnicas: 0,
    tasaRespuestaValida: 0,
    respuestasInvalidas: 0
  };

  barChartData: { datasets: { data: number[]; label: string }[]; labels: string[] } = { datasets: [{ data: [], label: 'Cantidad' }], labels: [] };
  barChartOptions = {
    responsive: true,
    indexAxis: 'x' as const, // barras verticales, etiquetas debajo de cada barra
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Respuestas por opción' }
    },
    scales: {
      x: {
        title: { display: true, text: 'Respuesta' },
        ticks: { autoSkip: false, maxRotation: 45, minRotation: 0 }
      },
      y: {
        title: { display: true, text: 'Cantidad' }
      }
    }
  };

  // Gráfico de torta
  pieChartData: { datasets: { data: number[]; backgroundColor: string[] }[]; labels: string[] } = { 
    datasets: [{ data: [], backgroundColor: [] }], 
    labels: [] 
  };
  pieChartOptions = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'bottom' as const
      },
      title: { 
        display: true, 
        text: 'Distribución porcentual de respuestas' 
      },
      tooltip: {
        callbacks: {
          label: function(context: any) {
            const total = context.dataset.data.reduce((sum: number, value: number) => sum + value, 0);
            const percentage = ((context.raw / total) * 100).toFixed(1);
            return `${context.label}: ${context.raw} (${percentage}%)`;
          }
        }
      }
    }
  };

  // Colores para el gráfico de torta
  private pieColors = [
    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
    '#FF9F40', '#FF6384', '#C9CBCF', '#4BC0C0', '#FF6384',
    '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40',
    '#E74C3C', '#3498DB', '#F39C12', '#2ECC71', '#9B59B6',
    '#E67E22', '#1ABC9C', '#34495E', '#F1C40F', '#E91E63'
  ];

  constructor(private estadisticasService: EstadisticasService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.cargarTodasLasCategorias();
    this.cargarTodasLasPreguntas();
  }

  ngAfterViewInit() {
    // Forzar detección de cambios después de que la vista se haya inicializado
    // Esto ayuda a resolver problemas de renderizado de dropdowns
    setTimeout(() => {
      this.cdr.detectChanges();
    }, 0);
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
    // Resetear la pregunta seleccionada cuando cambia la categoría
    this.preguntaSeleccionada = '';
    
    if (!this.filtros.categoria) {
      // Si selecciona 'Todas', eliminar el filtro y cargar todas las preguntas
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
      const tipo = pr.respuesta ? pr.respuesta.toString().trim() : '-';
      conteo[tipo] = (conteo[tipo] || 0) + 1;
    });

    const labels = Object.keys(conteo);
    const data = Object.values(conteo);

    // Actualizar gráfico de barras
    this.barChartData = {
      labels: labels,
      datasets: [{ data: data, label: 'Cantidad' }]
    };

    // Actualizar gráfico de torta
    this.pieChartData = {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: this.generatePieColors(labels.length)
      }]
    };

    // Calcular estadísticas resumidas
    this.calcularEstadisticasResumen(conteo, datosParaGraficar.length);
  }

  private calcularEstadisticasResumen(conteo: { [tipo: string]: number }, total: number) {
    // Total de respuestas
    this.estadisticasResumen.totalRespuestas = total;

    // Respuesta más común
    let maxCantidad = 0;
    let respuestaMasComun = '';
    
    Object.entries(conteo).forEach(([respuesta, cantidad]) => {
      if (cantidad > maxCantidad) {
        maxCantidad = cantidad;
        respuestaMasComun = respuesta;
      }
    });

    this.estadisticasResumen.respuestaMasComun = respuestaMasComun;
    this.estadisticasResumen.cantidadRespuestaMasComun = maxCantidad;

    // Respuestas únicas
    this.estadisticasResumen.respuestasUnicas = Object.keys(conteo).length;

    // Calcular tasa de respuesta válida (excluyendo respuestas como '-', 'no sabe', 'no contesta', etc.)
    const respuestasInvalidas = [
      '', 
      '-', 
      'no sabe', 
      'no contesta', 
      'ns/nc', 
      'n/a', 
      'sin respuesta', 
      'no sabe no contesta',
      'no sabe o no contesta',
      'ns nc',
      'no aplica',
      'no responde',
      'sin datos'
    ];
    
    let respuestasInvalidasCount = 0;

    Object.entries(conteo).forEach(([respuesta, cantidad]) => {
      const respuestaLower = respuesta.toLowerCase().trim();
      
      // Verificar coincidencia exacta o si es una respuesta claramente inválida
      const esInvalida = respuestasInvalidas.some(invalid => {
        const invalidLower = invalid.toLowerCase().trim();
        return respuestaLower === invalidLower || 
               (respuestaLower.length <= 3 && respuestaLower === invalidLower) || // Para casos como "-", ""
               (respuestaLower.startsWith('no sabe') && respuestaLower.includes('contesta')) ||
               (respuestaLower.includes('ns') && respuestaLower.includes('nc')) ||
               respuestaLower === 'n/a' ||
               respuestaLower === 'na';
      });
      
      if (esInvalida) {
        respuestasInvalidasCount += cantidad;
      }
    });

    this.estadisticasResumen.respuestasInvalidas = respuestasInvalidasCount;
    
    // Calcular el porcentaje de respuestas válidas
    const respuestasValidasCount = total - respuestasInvalidasCount;
    this.estadisticasResumen.tasaRespuestaValida = total > 0 ? 
      Math.round((respuestasValidasCount / total) * 100 * 100) / 100 : 0;
  }

  private generatePieColors(count: number): string[] {
    const colors = [];
    for (let i = 0; i < count; i++) {
      colors.push(this.pieColors[i % this.pieColors.length]);
    }
    return colors;
  }

  onFiltroChange() {
    // Solo actualiza el filtro en memoria, no carga datos
  }
}
