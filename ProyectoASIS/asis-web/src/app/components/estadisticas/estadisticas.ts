import { BaseChartDirective } from 'ng2-charts';
import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EstadisticasService, PreguntaRespuestaCategoria, Filtros, FiltroMultiple } from '../../services/estadisticas.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-estadisticas',
  templateUrl: './estadisticas.html',
  styleUrls: ['./estadisticas.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective]
})
export class EstadisticasComponent implements OnInit, AfterViewInit {

  // Exponer Math para usar en el template
  Math = Math;

  zonas: string[] = [];
  barrios: string[] = [];
  campanias: string[] = [];
  sexos: string[] = [];
  edades: number[] = [];
  edadRango: number[] = Array.from({length: 101}, (_, i) => i);
  preguntasRespuestas: PreguntaRespuestaCategoria[] = [];
  filtros: Filtros = {
    edadDesde: undefined,
    edadHasta: undefined,
    filtrosMultiples: []
  };
  categorias: string[] = [];

  // NUEVO: Filtros múltiples (categoría + pregunta + respuesta)
  filtrosMultiples: FiltroMultiple[] = [];
  
  // Campos para nuevo filtro múltiple
  nuevaCategoria: string = '';
  nuevaPregunta: string = '';
  nuevaRespuesta: string = '';
  
  // Datos dinámicos para filtros múltiples
  preguntasPorCategoria: string[] = [];
  respuestasPorPregunta: string[] = [];
  todasLasRespuestas: any[] = [];

  // Propiedades para tabla simplificada
  busquedaTexto: string = '';
  columnaOrdenamiento: string = '';
  direccionOrdenamiento: 'asc' | 'desc' = 'asc';
  
  // Control de estado de carga
  cargandoFiltros: boolean = false;
  paginaActual: number = 1;
  elementosPorPagina: number = 10;
  opcionesPaginacion: number[] = [5, 10, 20, 50];

  // Datos procesados para la tabla
  datosTabla: any[] = [];
  datosFiltrados: any[] = [];
  datosPaginados: any[] = [];

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
    indexAxis: 'y' as const, // barras verticales, etiquetas debajo de cada barra
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
    // NO cargar datos iniciales automáticamente
    // Los datos se cargarán solo cuando el usuario haga clic en "Aplicar filtros"
    
    // Inicializar tabla vacía
    this.inicializarTablaVacia();
  }

  ngAfterViewInit() {
    // Forzar detección de cambios después de que la vista se haya inicializado
    // Esto ayuda a resolver problemas de renderizado de dropdowns
    setTimeout(() => {
      this.cdr.detectChanges();
    }, 0);
  }

  aplicarFiltros(): void {
    // Evitar múltiples ejecuciones simultáneas
    if (this.cargandoFiltros) {
      return;
    }
    
    this.cargandoFiltros = true;
    
    // Edad: si no hay valor, eliminar del filtro
    if (!this.filtros.edadDesde) {
      delete this.filtros.edadDesde;
    }
    if (!this.filtros.edadHasta) {
      delete this.filtros.edadHasta;
    }
    
    // Agregar filtros múltiples si existen
    if (this.filtrosMultiples.length > 0) {
      this.filtros.filtrosMultiples = [...this.filtrosMultiples];
    } else {
      delete this.filtros.filtrosMultiples;
    }
    
    // Forzar detección de cambios antes de cargar datos
    this.cdr.detectChanges();
    
    // Usar setTimeout para asegurar que la detección de cambios se complete
    setTimeout(() => {
      this.cargarPreguntasRespuestas();
    }, 0);
  }

  // NUEVO: Métodos para filtros múltiples
  onCategoriaNuevaChange() {
    // Cuando cambia la categoría del nuevo filtro, cargar sus preguntas
    this.nuevaPregunta = '';
    this.nuevaRespuesta = '';
    this.respuestasPorPregunta = [];
    
    if (this.nuevaCategoria && this.nuevaCategoria !== 'TODAS') {
      // Categoría específica - filtrar preguntas de esa categoría solamente
      this.preguntasPorCategoria = Array.from(new Set(
        this.todasLasRespuestas
          .filter(pr => pr.categoria === this.nuevaCategoria)
          .map(pr => pr.pregunta)
          .filter(Boolean)
      ));
      // Forzar detección de cambios
      this.cdr.detectChanges();
    } else if (this.nuevaCategoria === 'TODAS') {
      // Todas las categorías - cargar todas las preguntas
      this.preguntasPorCategoria = Array.from(new Set(
        this.todasLasRespuestas
          .map(pr => pr.pregunta)
          .filter(Boolean)
      ));
      // Forzar detección de cambios
      this.cdr.detectChanges();
    } else {
      this.preguntasPorCategoria = [];
      this.cdr.detectChanges();
    }
  }

  onPreguntaNuevaChange() {
    // Cuando cambia la pregunta del nuevo filtro, cargar sus respuestas
    this.nuevaRespuesta = '';
    
    if (this.nuevaPregunta && this.nuevaCategoria) {
      if (this.nuevaPregunta === 'TODAS') {
        // Todas las preguntas - mostrar todas las respuestas de la categoría
        if (this.nuevaCategoria === 'TODAS') {
          this.respuestasPorPregunta = Array.from(new Set(
            this.todasLasRespuestas
              .map(pr => pr.respuesta)
              .filter(Boolean)
          ));
        } else {
          this.respuestasPorPregunta = Array.from(new Set(
            this.todasLasRespuestas
              .filter(pr => pr.categoria === this.nuevaCategoria)
              .map(pr => pr.respuesta)
              .filter(Boolean)
          ));
        }
        // Forzar detección de cambios
        this.cdr.detectChanges();
      } else {
        // Pregunta específica
        if (this.nuevaCategoria === 'TODAS') {
          this.respuestasPorPregunta = Array.from(new Set(
            this.todasLasRespuestas
              .filter(pr => pr.pregunta === this.nuevaPregunta)
              .map(pr => pr.respuesta)
              .filter(Boolean)
          ));
        } else {
          // Categoría específica + Pregunta específica
          this.respuestasPorPregunta = Array.from(new Set(
            this.todasLasRespuestas
              .filter(pr => pr.categoria === this.nuevaCategoria && pr.pregunta === this.nuevaPregunta)
              .map(pr => pr.respuesta)
              .filter(Boolean)
          ));
        }
        // Forzar detección de cambios
        this.cdr.detectChanges();
      }
    } else {
      this.respuestasPorPregunta = [];
      this.cdr.detectChanges();
    }
  }

  agregarFiltroMultiple() {
    if (this.nuevaCategoria && this.nuevaPregunta && this.nuevaRespuesta) {
      // Verificar que no existe ya este filtro
      const existe = this.filtrosMultiples.find(f => 
        f.categoria === this.nuevaCategoria && 
        f.pregunta === this.nuevaPregunta && 
        f.respuesta === this.nuevaRespuesta
      );
      
      if (!existe) {
        this.filtrosMultiples.push({
          categoria: this.nuevaCategoria,
          pregunta: this.nuevaPregunta,
          respuesta: this.nuevaRespuesta
        });
        
        // Limpiar campos
        this.nuevaCategoria = '';
        this.nuevaPregunta = '';
        this.nuevaRespuesta = '';
        this.preguntasPorCategoria = [];
        this.respuestasPorPregunta = [];
        
        // Forzar detección de cambios
        this.cdr.detectChanges();
      }
    }
  }

  eliminarFiltroMultiple(index: number) {
    this.filtrosMultiples.splice(index, 1);
    
    // Auto-actualizar la tabla solo si quedan filtros múltiples
    if (this.tieneAlgunFiltroActivo()) {
      this.aplicarFiltros();
    } else {
      // Si no quedan filtros múltiples, limpiar la tabla
      this.inicializarTablaVacia();
      this.preguntasRespuestas = [];
      this.actualizarDatosGrafico();
    }
  }

  limpiarTodosFiltros() {
    this.filtrosMultiples = [];
    // También limpiar los campos del formulario de nuevo filtro
    this.nuevaCategoria = '';
    this.nuevaPregunta = '';
    this.nuevaRespuesta = '';
    this.preguntasPorCategoria = [];
    this.respuestasPorPregunta = [];
    
    // Como no quedan filtros múltiples, limpiar la tabla
    this.inicializarTablaVacia();
    this.preguntasRespuestas = [];
    this.actualizarDatosGrafico();
    
    // Forzar detección de cambios
    this.cdr.detectChanges();
  }

  limpiarRangoEdad() {
    this.filtros.edadDesde = undefined;
    this.filtros.edadHasta = undefined;
    
    // Si hay filtros múltiples activos, recargar la tabla con los filtros actualizados
    if (this.filtrosMultiples.length > 0) {
      this.aplicarFiltros();
    }
    
    // Forzar detección de cambios
    this.cdr.detectChanges();
  }

  tieneAlgunFiltroActivo(): boolean {
    // Solo permitir aplicar filtros si hay al menos un filtro múltiple completo
    // El filtro de edad por sí solo no es suficiente
    const tieneFiltrosMultiples = this.filtrosMultiples.length > 0;
    
    return tieneFiltrosMultiples;
  }

  // Función para mejorar el rendimiento de los dropdowns
  trackByIndex(index: number, item: any): any {
    return index;
  }

  cargarTodasLasCategorias() {
    this.estadisticasService.filtrarPreguntasRespuestas({}).subscribe(data => {
      this.categorias = Array.from(new Set(data.map(pr => pr.categoria).filter(Boolean)));
      this.todasLasRespuestas = data; // Guardar todas las respuestas para filtros múltiples
      
      // Forzar detección de cambios con setTimeout para asegurar que se ejecute
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 0);
    });
  }

  cargarPreguntasRespuestas() {
    // Crear una copia del filtro para enviar al backend
    const filtrosParaBackend = { ...this.filtros };
    
    // Si existe 'campaña', renombrar a 'campania' antes de enviar
    if ((filtrosParaBackend as any)['campaña']) {
      (filtrosParaBackend as any)['campania'] = (filtrosParaBackend as any)['campaña'];
      delete (filtrosParaBackend as any)['campaña'];
    }
    
    // Si categoria es string vacío, no enviarla al backend
    if (filtrosParaBackend.categoria === '') {
      delete filtrosParaBackend.categoria;
    }
    
    this.estadisticasService.filtrarPreguntasRespuestas(filtrosParaBackend).subscribe(data => {
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

      // Actualizar datos y tabla de manera síncrona
      this.actualizarDatosGrafico();
      this.procesarDatosTabla();
      
      // Liberar el estado de carga
      this.cargandoFiltros = false;
      
      // Forzar detección de cambios múltiples veces para asegurar actualización
      this.cdr.detectChanges();
      
      // Usar setTimeout para asegurar que la vista se actualice completamente
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 0);
    });
  }

  actualizarDatosGrafico() {
    // Verificar los diferentes tipos de filtros (igual que en generarDatosTabla)
    const hayFiltrosMultiples = this.filtrosMultiples.length > 0;
    
    if (!hayFiltrosMultiples) {
      this.generarGraficoComportamientoNormal();
      return;
    }
    
    const todosSonEspecificos = this.filtrosMultiples.every(f => 
      f.categoria !== 'TODAS' && f.pregunta !== 'TODAS' && f.respuesta !== 'TODAS'
    );
    
    const todosSonTodas = this.filtrosMultiples.every(f => 
      f.categoria === 'TODAS' && f.pregunta === 'TODAS' && f.respuesta === 'TODAS'
    );
    
    const esMixto = !todosSonEspecificos && !todosSonTodas;
    
    if (todosSonEspecificos) {
      // CASO 1: Filtros específicos - mostrar personas únicas
      this.generarGraficoFiltrosEspecificos();
    } else if (todosSonTodas) {
      // CASO 2: Todos "TODAS" - desglose normal
      this.generarGraficoComportamientoNormal();
    } else {
      // CASO 3: Mixto - desglose filtrado
      this.generarGraficoMixto();
    }
  }

  generarGraficoFiltrosEspecificos() {
    // Contar encuestaId únicos (igual que en tabla)
    const encuestaIdsUnicos = new Set();
    
    this.preguntasRespuestas.forEach(pr => {
      const encuestaId = pr.encuestaId || pr.encuesta_id || (pr as any).id;
      if (encuestaId) {
        encuestaIdsUnicos.add(encuestaId);
      }
    });
    
    const totalPersonasUnicas = encuestaIdsUnicos.size;
    
    const descripcionFiltros = this.filtrosMultiples.map(f => 
      `${f.categoria} > ${f.pregunta}: ${f.respuesta}`
    ).join(' Y ');
    
    const labels = [`Personas que cumplen filtros específicos`];
    const data = [totalPersonasUnicas];

    this.barChartData = {
      labels: labels,
      datasets: [{ data: data, label: 'Personas únicas' }]
    };

    this.pieChartData = {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: ['#1e5a5a']
      }]
    };

    this.calcularEstadisticasResumenFiltros(totalPersonasUnicas, descripcionFiltros);
  }

  generarGraficoMixto() {
    // CASO 3: Mixto - mostrar desglose solo de los filtros que tienen "TODAS"
    
    // Identificar qué filtros tienen "TODAS"
    const filtrosConTodas = this.filtrosMultiples.filter(f => 
      f.categoria === 'TODAS' || f.pregunta === 'TODAS' || f.respuesta === 'TODAS'
    );
    
    // Filtrar datos para mostrar solo las respuestas que corresponden a los filtros "TODAS"
    const datosParaDesglose = this.preguntasRespuestas.filter(pr => {
      return filtrosConTodas.some(filtro => {
        const categoriaMatch = filtro.categoria === 'TODAS' || pr.categoria === filtro.categoria;
        const preguntaMatch = filtro.pregunta === 'TODAS' || pr.pregunta === filtro.pregunta;
        const respuestaMatch = filtro.respuesta === 'TODAS' || pr.respuesta === filtro.respuesta;
        
        return categoriaMatch && preguntaMatch && respuestaMatch;
      });
    });
    
    // Agrupar por tipo de respuesta (solo los datos filtrados)
    const conteo: { [tipo: string]: number } = {};
    datosParaDesglose.forEach(pr => {
      const tipo = pr.respuesta ? pr.respuesta.toString().trim() : '-';
      conteo[tipo] = (conteo[tipo] || 0) + 1;
    });

    const labels = Object.keys(conteo);
    const data = Object.values(conteo);

    this.barChartData = {
      labels: labels,
      datasets: [{ data: data, label: 'Cantidad' }]
    };

    this.pieChartData = {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: this.generatePieColors(labels.length)
      }]
    };

    this.calcularEstadisticasResumen(conteo, datosParaDesglose.length);
  }

  generarGraficoComportamientoNormal() {
    // Agrupar por tipo de respuesta
    const conteo: { [tipo: string]: number } = {};
    this.preguntasRespuestas.forEach(pr => {
      const tipo = pr.respuesta ? pr.respuesta.toString().trim() : '-';
      conteo[tipo] = (conteo[tipo] || 0) + 1;
    });

    const labels = Object.keys(conteo);
    const data = Object.values(conteo);

    this.barChartData = {
      labels: labels,
      datasets: [{ data: data, label: 'Cantidad' }]
    };

    this.pieChartData = {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: this.generatePieColors(labels.length)
      }]
    };

    this.calcularEstadisticasResumen(conteo, this.preguntasRespuestas.length);
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

  private calcularEstadisticasResumenFiltros(totalPersonas: number, descripcionFiltros: string) {
    // Estadísticas específicas para cuando hay filtros múltiples
    this.estadisticasResumen.totalRespuestas = totalPersonas;
    this.estadisticasResumen.respuestaMasComun = 'Personas que cumplen filtros';
    this.estadisticasResumen.cantidadRespuestaMasComun = totalPersonas;
    this.estadisticasResumen.respuestasUnicas = 1;
    this.estadisticasResumen.tasaRespuestaValida = 100;
    this.estadisticasResumen.respuestasInvalidas = 0;
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

  procesarDatosTabla() {
    this.generarDatosTabla();
    this.aplicarFiltrosBusqueda();
    this.aplicarOrdenamiento();
    this.aplicarPaginacion();
    
    // Forzar detección de cambios después de procesar la tabla
    this.cdr.detectChanges();
  }

  generarDatosTabla() {
    // Verificar los diferentes tipos de filtros
    const hayFiltrosMultiples = this.filtrosMultiples.length > 0;
    
    if (!hayFiltrosMultiples) {
      // Sin filtros múltiples: comportamiento normal
      this.generarTablaComportamientoNormal();
      return;
    }
    
    // Clasificar el tipo de filtros
    const todosSonEspecificos = this.filtrosMultiples.every(f => 
      f.categoria !== 'TODAS' && f.pregunta !== 'TODAS' && f.respuesta !== 'TODAS'
    );
    
    const todosSonTodas = this.filtrosMultiples.every(f => 
      f.categoria === 'TODAS' && f.pregunta === 'TODAS' && f.respuesta === 'TODAS'
    );
    
    const esMixto = !todosSonEspecificos && !todosSonTodas;
    
    console.log('=== ANÁLISIS DE FILTROS ===');
    console.log('Filtros aplicados:', this.filtrosMultiples);
    console.log('Todos específicos:', todosSonEspecificos);
    console.log('Todos TODAS:', todosSonTodas);
    console.log('Es mixto:', esMixto);
    console.log('Total registros:', this.preguntasRespuestas.length);
    
    if (todosSonEspecificos) {
      // CASO 1: Filtros específicos - contar personas únicas
      this.generarTablaFiltrosEspecificos();
    } else if (todosSonTodas) {
      // CASO 2: Todos "TODAS" - desglose normal
      this.generarTablaComportamientoNormal();
    } else {
      // CASO 3: Mixto - desglose filtrado
      this.generarTablaMixto();
    }
  }

  generarTablaFiltrosEspecificos() {
    // Contar encuestaId únicos
    const encuestaIdsUnicos = new Set();
    
    this.preguntasRespuestas.forEach((pr, index) => {
      const encuestaId = pr.encuestaId || pr.encuesta_id || (pr as any).id;
      
      if (encuestaId) {
        encuestaIdsUnicos.add(encuestaId);
      }
      
      if (index < 3) {
        console.log(`[ESPECÍFICOS] Registro ${index + 1}:`, {
          encuestaId: encuestaId,
          categoria: pr.categoria,
          pregunta: pr.pregunta,
          respuesta: pr.respuesta
        });
      }
    });
    
    const totalPersonasUnicas = encuestaIdsUnicos.size;
    console.log('Personas únicas encontradas:', totalPersonasUnicas);
    
    const filtrosDescripcion = this.filtrosMultiples.map(f => 
      `${f.categoria} > ${f.pregunta}: ${f.respuesta}`
    ).join(' Y ');
    
    this.datosTabla = [{
      respuesta: `Personas que cumplen: ${filtrosDescripcion}`,
      cantidad: totalPersonasUnicas,
      porcentaje: '100.00',
      categorias: this.filtrosMultiples.map(f => f.categoria).join(', '),
      preguntas: this.filtrosMultiples.length,
      ranking: 1
    }];
  }

  generarTablaMixto() {
    // CASO 3: Mixto - mostrar desglose solo de los filtros que tienen "TODAS"
    console.log('[MIXTO] Generando desglose solo de filtros con TODAS');
    
    // Identificar qué filtros tienen "TODAS" y cuáles son específicos
    const filtrosConTodas = this.filtrosMultiples.filter(f => 
      f.categoria === 'TODAS' || f.pregunta === 'TODAS' || f.respuesta === 'TODAS'
    );
    
    const filtrosEspecificos = this.filtrosMultiples.filter(f => 
      f.categoria !== 'TODAS' && f.pregunta !== 'TODAS' && f.respuesta !== 'TODAS'
    );
    
    console.log('[MIXTO] Filtros con TODAS:', filtrosConTodas);
    console.log('[MIXTO] Filtros específicos:', filtrosEspecificos);
    
    // Filtrar datos para mostrar solo las respuestas que corresponden a los filtros "TODAS"
    const datosParaDesglose = this.preguntasRespuestas.filter(pr => {
      // Solo incluir registros que correspondan a preguntas de filtros con "TODAS"
      return filtrosConTodas.some(filtro => {
        // Verificar si esta respuesta corresponde a un filtro "TODAS"
        const categoriaMatch = filtro.categoria === 'TODAS' || pr.categoria === filtro.categoria;
        const preguntaMatch = filtro.pregunta === 'TODAS' || pr.pregunta === filtro.pregunta;
        const respuestaMatch = filtro.respuesta === 'TODAS' || pr.respuesta === filtro.respuesta;
        
        return categoriaMatch && preguntaMatch && respuestaMatch;
      });
    });
    
    console.log('[MIXTO] Datos filtrados para desglose:', datosParaDesglose.length);
    console.log('[MIXTO] Ejemplos de datos para desglose:', datosParaDesglose.slice(0, 3));
    
    // Agrupar por tipo de respuesta (solo los datos filtrados)
    const resumenPorRespuesta: { [key: string]: any } = {};
    
    datosParaDesglose.forEach((pr, index) => {
      const respuesta = pr.respuesta || '-';
      if (!resumenPorRespuesta[respuesta]) {
        resumenPorRespuesta[respuesta] = {
          respuesta: respuesta,
          cantidad: 0,
          porcentaje: 0,
          categorias: new Set(),
          preguntas: new Set()
        };
      }
      resumenPorRespuesta[respuesta].cantidad++;
      resumenPorRespuesta[respuesta].categorias.add(pr.categoria || '-');
      resumenPorRespuesta[respuesta].preguntas.add(pr.pregunta || '-');
      
      if (index < 3) {
        console.log(`[MIXTO] Registro procesado ${index + 1}:`, {
          categoria: pr.categoria,
          pregunta: pr.pregunta,
          respuesta: pr.respuesta
        });
      }
    });

    const total = datosParaDesglose.length;
    this.datosTabla = Object.values(resumenPorRespuesta).map((item: any) => ({
      respuesta: item.respuesta,
      cantidad: item.cantidad,
      porcentaje: total > 0 ? ((item.cantidad / total) * 100).toFixed(2) : '0',
      categorias: Array.from(item.categorias).join(', '),
      preguntas: Array.from(item.preguntas).length,
      ranking: 0
    })).sort((a, b) => b.cantidad - a.cantidad)
      .map((item, index) => ({ ...item, ranking: index + 1 }));
    
    console.log('[MIXTO] Desglose final generado:', this.datosTabla);
  }

  generarTablaComportamientoNormal() {
    // Comportamiento normal: agrupar por respuesta
    const resumenPorRespuesta: { [key: string]: any } = {};
    
    this.preguntasRespuestas.forEach(pr => {
      const respuesta = pr.respuesta || '-';
      if (!resumenPorRespuesta[respuesta]) {
        resumenPorRespuesta[respuesta] = {
          respuesta: respuesta,
          cantidad: 0,
          porcentaje: 0,
          categorias: new Set(),
          preguntas: new Set()
        };
      }
      resumenPorRespuesta[respuesta].cantidad++;
      resumenPorRespuesta[respuesta].categorias.add(pr.categoria || '-');
      resumenPorRespuesta[respuesta].preguntas.add(pr.pregunta || '-');
    });

    const total = this.preguntasRespuestas.length;
    this.datosTabla = Object.values(resumenPorRespuesta).map((item: any) => ({
      respuesta: item.respuesta,
      cantidad: item.cantidad,
      porcentaje: total > 0 ? ((item.cantidad / total) * 100).toFixed(2) : '0',
      categorias: Array.from(item.categorias).join(', '),
      preguntas: Array.from(item.preguntas).length,
      ranking: 0
    })).sort((a, b) => b.cantidad - a.cantidad)
      .map((item, index) => ({ ...item, ranking: index + 1 }));
  }

  aplicarFiltrosBusqueda() {
    if (this.busquedaTexto.trim()) {
      const termino = this.busquedaTexto.toLowerCase().trim();
      this.datosFiltrados = this.datosTabla.filter(item => 
        Object.values(item).some(valor => 
          valor?.toString().toLowerCase().includes(termino)
        )
      );
    } else {
      this.datosFiltrados = [...this.datosTabla];
    }
  }

  aplicarOrdenamiento() {
    if (!this.columnaOrdenamiento) return;

    this.datosFiltrados.sort((a, b) => {
      const valorA = a[this.columnaOrdenamiento];
      const valorB = b[this.columnaOrdenamiento];

      let comparacion = 0;
      if (typeof valorA === 'number' && typeof valorB === 'number') {
        comparacion = valorA - valorB;
      } else {
        comparacion = valorA?.toString().localeCompare(valorB?.toString()) || 0;
      }

      return this.direccionOrdenamiento === 'asc' ? comparacion : -comparacion;
    });
  }

  aplicarPaginacion() {
    // Asegurar que elementosPorPagina sea un número
    const elementosNumero = Number(this.elementosPorPagina);
    const paginaNumero = Number(this.paginaActual);
    
    const inicio = (paginaNumero - 1) * elementosNumero;
    const fin = inicio + elementosNumero;
    this.datosPaginados = this.datosFiltrados.slice(inicio, fin);
  }

  ordenarPorColumna(columna: string) {
    if (this.columnaOrdenamiento === columna) {
      this.direccionOrdenamiento = this.direccionOrdenamiento === 'asc' ? 'desc' : 'asc';
    } else {
      this.columnaOrdenamiento = columna;
      this.direccionOrdenamiento = 'asc';
    }
    this.aplicarOrdenamiento();
    this.aplicarPaginacion();
  }

  cambiarPagina(pagina: number) {
    this.paginaActual = pagina;
    this.aplicarPaginacion();
    // Forzar detección de cambios
    this.cdr.detectChanges();
  }

  cambiarElementosPorPagina(cantidad: number) {
    // Asegurar que cantidad sea un número
    const cantidadNumero = Number(cantidad);
    this.elementosPorPagina = cantidadNumero;
    this.paginaActual = 1;
    this.aplicarPaginacion();
    // Forzar detección de cambios
    this.cdr.detectChanges();
  }

  onBusquedaCambio() {
    this.paginaActual = 1;
    this.aplicarFiltrosBusqueda();
    this.aplicarOrdenamiento();
    this.aplicarPaginacion();
  }

  get totalPaginas(): number {
    const elementosNumero = Number(this.elementosPorPagina);
    return Math.ceil(this.datosFiltrados.length / elementosNumero);
  }

  get paginasDisponibles(): number[] {
    const total = this.totalPaginas;
    const actual = this.paginaActual;
    const rango = 2;
    
    let inicio = Math.max(1, actual - rango);
    let fin = Math.min(total, actual + rango);
    
    if (fin - inicio < 4) {
      if (inicio === 1) {
        fin = Math.min(total, inicio + 4);
      } else {
        inicio = Math.max(1, fin - 4);
      }
    }
    
    const paginas = [];
    for (let i = inicio; i <= fin; i++) {
      paginas.push(i);
    }
    return paginas;
  }

  /**
   * Inicializa la tabla con datos vacíos para que no se muestren datos hasta aplicar filtros
   */
  inicializarTablaVacia() {
    this.datosTabla = [];
    this.datosFiltrados = [];
    this.datosPaginados = [];
    this.preguntasRespuestas = [];
    this.paginaActual = 1;
    
    // Resetear estadísticas
    this.estadisticasResumen = {
      totalRespuestas: 0,
      respuestaMasComun: '',
      cantidadRespuestaMasComun: 0,
      respuestasUnicas: 0,
      tasaRespuestaValida: 0,
      respuestasInvalidas: 0
    };

    // Limpiar gráficos
    this.barChartData = { datasets: [{ data: [], label: 'Cantidad' }], labels: [] };
    this.pieChartData = { datasets: [{ data: [], backgroundColor: [] }], labels: [] };
  }

  // Función helper para truncar texto largo
  truncateText(text: string, maxLength: number = 50): string {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }
}
