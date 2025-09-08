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

  // Exponer Math para usar en el template
  Math = Math;

  zonas: string[] = [];
  barrios: string[] = [];
  campanias: string[] = [];
  sexos: string[] = [];
  edades: number[] = [];
  edadRango: number[] = Array.from({length: 101}, (_, i) => i);
  preguntas: string[] = [];
  preguntaSeleccionada: string = '';
  preguntasRespuestas: PreguntaRespuestaCategoria[] = [];
  filtros: Filtros = {
    categoria: '' // Inicializar con string vacío para que muestre "Todas las categorías"
  };
  categorias: string[] = [];

  // Propiedades para tabla simplificada
  busquedaTexto: string = '';
  columnaOrdenamiento: string = '';
  direccionOrdenamiento: 'asc' | 'desc' = 'asc';
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
    this.cargarTodasLasPreguntas();
    // NO cargar datos iniciales - solo mostrarlos cuando se apliquen filtros
    // this.cargarPreguntasRespuestas(); // Comentado para no cargar datos por defecto
    
    // Inicializar con datos vacíos para mostrar interfaz limpia
    this.preguntasRespuestas = [];
    this.datosTabla = [];
    this.datosFiltrados = [];
    this.datosPaginados = [];
    this.actualizarDatosGrafico();
    this.procesarDatosTabla();
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
    // No eliminar la propiedad categoria, solo asegurarse de que sea string vacío si no hay selección
    if (!this.filtros.categoria) {
      this.filtros.categoria = '';
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
    
    if (!this.filtros.categoria || this.filtros.categoria === '') {
      // Si selecciona 'Todas', establecer como string vacío y cargar todas las preguntas
      this.filtros.categoria = '';
      this.cargarTodasLasPreguntas();
    } else {
      // Si hay categoría, filtrar solo preguntas por esa categoría
      this.estadisticasService.filtrarPreguntasRespuestas({ categoria: this.filtros.categoria }).subscribe(data => {
        this.preguntas = Array.from(new Set(data.map(pr => pr.pregunta).filter(Boolean)));
        // Forzar detección de cambios
        this.cdr.detectChanges();
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
      
      // Asegurar que el dropdown tenga el valor inicial correcto
      if (!this.preguntaSeleccionada) {
        this.preguntaSeleccionada = '';
      }
      
      // Forzar detección de cambios después de cargar las preguntas
      this.cdr.detectChanges();
    });
  }

  cargarTodasLasCategorias() {
    this.estadisticasService.filtrarPreguntasRespuestas({}).subscribe(data => {
      this.categorias = Array.from(new Set(data.map(pr => pr.categoria).filter(Boolean)));
      
      // Asegurar que el dropdown tenga el valor inicial correcto
      if (!this.filtros.categoria) {
        this.filtros.categoria = '';
      }
      
      // Forzar detección de cambios después de cargar las categorías
      this.cdr.detectChanges();
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

      this.actualizarDatosGrafico();
      this.procesarDatosTabla();
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

  procesarDatosTabla() {
    this.generarDatosTabla();
    this.aplicarFiltrosBusqueda();
    this.aplicarOrdenamiento();
    this.aplicarPaginacion();
  }

  generarDatosTabla() {
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
}
