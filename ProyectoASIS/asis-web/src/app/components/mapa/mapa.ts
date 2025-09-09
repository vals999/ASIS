import { Component, OnInit, AfterViewInit, Inject, PLATFORM_ID, OnDestroy, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MapaService, Coordenada } from '../../services/mapa.service';
import { EstadisticasService, FiltroMultiple, Filtros } from '../../services/estadisticas.service';
import { CargaArchivosComponent } from '../carga-archivos/carga-archivos';
import { EventService } from '../../services/event.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule, FormsModule, CargaArchivosComponent],
  templateUrl: './mapa.html',
  styleUrl: './mapa.css'
})
export class MapaComponent implements OnInit, AfterViewInit, OnDestroy {
  private map: any;
  public isBrowser: boolean;
  private coordenadas: Coordenada[] = [];
  public cargandoDatos = false;
  private csvUploadSubscription?: Subscription;
  private showMapSubscription?: Subscription;
  private markersGroup: any;
  private markerClusterGroup: any;
  private heatmapLayer: any;
  public csvCargado = false;
  
  // Signal para controlar si hay datos disponibles (como en carga-archivos)
  public datosDisponibles = signal<boolean>(false);
  
  // Signal para controlar el tipo de marcador (pines, clusters o heatmap)
  public tipoMarcador = signal<'pines' | 'clusters' | 'heatmap'>('pines');

  // === FILTROS MÚLTIPLES (igual que en estadísticas) ===
  public filtros: Filtros = {};
  public filtrosMultiples: FiltroMultiple[] = [];
  
  // Datos para dropdowns
  public categorias: string[] = [];
  public preguntasPorCategoria: string[] = [];
  public respuestasPorPregunta: string[] = [];
  public todasLasRespuestas: any[] = [];
  
  // Campos para nuevo filtro
  public nuevaCategoria: string = '';
  public nuevaPregunta: string = '';
  public nuevaRespuesta: string = '';
  
  // Estado de carga
  public cargandoFiltros = false;

  // Información sobre encuestas sin coordenadas
  public encuestasSinCoordenadas = signal<number>(0);
  public totalEncuestasFiltradas = signal<number>(0);

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private mapaService: MapaService,
    private estadisticasService: EstadisticasService,
    private eventService: EventService,
    private cdr: ChangeDetectorRef
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      // Cargar todas las categorías al inicializar
      this.cargarTodasLasCategorias();
      
      // Verificar si ya existen datos al cargar el componente
      this.verificarDatosExistentes();
      
      // Suscribirse al evento de carga de CSV (solo para saber que se cargó)
      this.csvUploadSubscription = this.eventService.csvUploaded$.subscribe(() => {
        console.log('CSV cargado, esperando instrucción para mostrar mapa...');
        this.csvCargado = true;
        this.datosDisponibles.set(true);
        // Recargar categorías después de cargar CSV
        this.cargarTodasLasCategorias();
      });

      // Suscribirse al evento para mostrar el mapa
      this.showMapSubscription = this.eventService.showMap$.subscribe(() => {
        console.log('Solicitado mostrar coordenadas en el mapa...');
        this.cargarYMostrarCoordenadas();
      });
    }
  }

  ngAfterViewInit(): void {
    if (this.isBrowser) {
      this.loadLeafletAndInitMap();
    }
  }

  ngOnDestroy(): void {
    if (this.csvUploadSubscription) {
      this.csvUploadSubscription.unsubscribe();
    }
    if (this.showMapSubscription) {
      this.showMapSubscription.unsubscribe();
    }
  }

  private verificarDatosExistentes(): void {
    this.mapaService.verificarSiExistenDatos().subscribe({
      next: (response) => {
        if (response.existenDatos) {
          console.log(`Se encontraron ${response.totalRespuestas} respuestas en la base de datos`);
          this.datosDisponibles.set(true);
        } else {
          console.log('No se encontraron datos en la base de datos');
          this.datosDisponibles.set(false);
        }
      },
      error: (error) => {
        console.error('Error al verificar datos existentes:', error);
        this.datosDisponibles.set(false);
      }
    });
  }

  cambiarTipoMarcador(tipo: 'pines' | 'clusters' | 'heatmap'): void {
    this.tipoMarcador.set(tipo);
    console.log(`Cambiando tipo de marcador a: ${tipo}`);
    
    // Si hay coordenadas cargadas, actualizar la visualización
    if (this.coordenadas.length > 0 && this.map) {
      // Limpiar todo antes de mostrar el nuevo tipo
      this.limpiarMarcadores();
      
      // Mostrar el nuevo tipo
      if (tipo === 'clusters') {
        this.mostrarClusters();
      } else if (tipo === 'heatmap') {
        this.mostrarHeatmap();
      } else {
        this.mostrarPines();
      }
    }
  }

  private cargarYMostrarCoordenadas(): void {
    this.cargandoDatos = true;
    this.mapaService.obtenerCoordenadasMapa().subscribe({
      next: (coordenadasDTO) => {
        this.coordenadas = this.mapaService.procesarCoordenadas(coordenadasDTO);
        console.log('Coordenadas procesadas:', this.coordenadas);
        
        // Resetear información de filtros cuando se cargan todas las coordenadas
        this.totalEncuestasFiltradas.set(0);
        this.encuestasSinCoordenadas.set(0);
        
        this.cargandoDatos = false;
        
        // Si el mapa ya está inicializado, agregar los marcadores
        if (this.map) {
          this.agregarMarcadores();
        }
      },
      error: (error) => {
        console.error('Error al cargar coordenadas:', error);
        this.cargandoDatos = false;
      }
    });
  }

  private async loadLeafletAndInitMap(): Promise<void> {
    try {
      // Importación dinámica de Leaflet y plugin de clustering
      const L = await import('leaflet');
      
      // Cargar el plugin de markercluster dinámicamente
      await this.loadMarkerClusterPlugin();
      
      // Cargar el plugin de heatmap dinámicamente
      await this.loadHeatmapPlugin();
      
      // Configurar iconos por defecto de Leaflet
      delete (L as any).Icon.Default.prototype._getIconUrl;
      L.Icon.Default.mergeOptions({
        iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
        iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
      });

      this.initMap(L);
    } catch (error) {
      console.error('Error loading Leaflet:', error);
    }
  }

  private async loadMarkerClusterPlugin(): Promise<void> {
    try {
      // Cargar CSS del plugin de clustering
      if (!document.querySelector('link[href*="markercluster"]')) {
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = 'https://unpkg.com/leaflet.markercluster@1.4.1/dist/MarkerCluster.css';
        document.head.appendChild(link);

        const linkDefault = document.createElement('link');
        linkDefault.rel = 'stylesheet';
        linkDefault.href = 'https://unpkg.com/leaflet.markercluster@1.4.1/dist/MarkerCluster.Default.css';
        document.head.appendChild(linkDefault);
      }

      // Cargar JavaScript del plugin
      if (!(window as any).L || !(window as any).L.markerClusterGroup) {
        await this.loadScript('https://unpkg.com/leaflet.markercluster@1.4.1/dist/leaflet.markercluster.js');
      }
    } catch (error) {
      console.error('Error loading MarkerCluster plugin:', error);
    }
  }

  private async loadHeatmapPlugin(): Promise<void> {
    try {
      // Cargar JavaScript del plugin de heatmap
      if (!(window as any).L || !(window as any).L.heatLayer) {
        await this.loadScript('https://cdn.jsdelivr.net/npm/leaflet.heat@0.2.0/dist/leaflet-heat.js');
      }
    } catch (error) {
      console.error('Error loading Heatmap plugin:', error);
    }
  }

  private loadScript(src: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (document.querySelector(`script[src="${src}"]`)) {
        resolve();
        return;
      }

      const script = document.createElement('script');
      script.src = src;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error(`Failed to load script: ${src}`));
      document.head.appendChild(script);
    });
  }

  private initMap(L: any): void {
    // Inicializar el mapa centrado en La Plata, Argentina
    this.map = L.map('map').setView([-34.9215, -57.9545], 13);

    // Agregar capa de tiles de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    // Forzar el redimensionamiento del mapa después de un breve delay
    setTimeout(() => {
      this.map.invalidateSize();
      
      // Si ya tenemos coordenadas, agregar los marcadores
      if (this.coordenadas.length > 0) {
        this.agregarMarcadores();
      }
    }, 100);
  }

  private async agregarMarcadores(): Promise<void> {
    if (!this.map || this.coordenadas.length === 0) {
      console.log('Sin coordenadas para mostrar en el mapa');
      return;
    }

    // Limpiar marcadores existentes ANTES de agregar nuevos
    this.limpiarMarcadores();
    
    // Mostrar según el tipo seleccionado
    if (this.tipoMarcador() === 'clusters') {
      await this.mostrarClusters();
    } else if (this.tipoMarcador() === 'heatmap') {
      await this.mostrarHeatmap();
    } else {
      await this.mostrarPines();
    }
  }

  private async mostrarPines(): Promise<void> {
    const L = await import('leaflet');
    
    // Asegurar que no hay clusters antes de crear pines
    if (this.markerClusterGroup) {
      this.markerClusterGroup.clearLayers();
      this.map.removeLayer(this.markerClusterGroup);
      this.markerClusterGroup = null;
    }
    
    // Crear un nuevo grupo de marcadores normal
    this.markersGroup = L.layerGroup().addTo(this.map);

    // Agrupar coordenadas por ubicación exacta para aplicar jitter
    const coordenadasPorUbicacion = new Map<string, any[]>();
    
    this.coordenadas.forEach(coordenada => {
      if (this.validarCoordenadas(coordenada.latitud, coordenada.longitud)) {
        const key = `${coordenada.latitud.toFixed(6)},${coordenada.longitud.toFixed(6)}`;
        if (!coordenadasPorUbicacion.has(key)) {
          coordenadasPorUbicacion.set(key, []);
        }
        coordenadasPorUbicacion.get(key)!.push(coordenada);
      }
    });

    // Agregar jitter (dispersión) a coordenadas duplicadas y crear marcadores
    let marcadoresAgregados = 0;
    coordenadasPorUbicacion.forEach((encuestas, ubicacion) => {
      const [lat, lng] = ubicacion.split(',').map(Number);
      
      if (encuestas.length === 1) {
        // Marcador único - sin jitter
        const marker = L.marker([lat, lng])
          .bindPopup(`
            <div>
              <strong>Encuesta ID:</strong> ${encuestas[0].encuestaId}<br>
              <strong>Latitud:</strong> ${lat.toFixed(6)}<br>
              <strong>Longitud:</strong> ${lng.toFixed(6)}
            </div>
          `);
        this.markersGroup.addLayer(marker);
        marcadoresAgregados++;
      } else {
        // Múltiples encuestas en la misma ubicación - aplicar jitter
        encuestas.forEach((encuesta, index) => {
          const jitterRange = 0.0001; // Aproximadamente 11 metros
          const jitterLat = lat + (Math.random() - 0.5) * jitterRange;
          const jitterLng = lng + (Math.random() - 0.5) * jitterRange;
          
          const marker = L.marker([jitterLat, jitterLng])
            .bindPopup(`
              <div>
                <strong>Encuesta ID:</strong> ${encuesta.encuestaId}<br>
                <strong>Ubicación:</strong> ${encuestas.length} encuesta(s) en esta área<br>
                <strong>Latitud original:</strong> ${lat.toFixed(6)}<br>
                <strong>Longitud original:</strong> ${lng.toFixed(6)}
              </div>
            `);
          this.markersGroup.addLayer(marker);
          marcadoresAgregados++;
        });
      }
    });

    console.log(`${marcadoresAgregados} marcadores agregados al mapa`);
    this.ajustarVistaAMarcadores();
  }

  private async mostrarClusters(): Promise<void> {
    const L = await import('leaflet');
    
    // Asegurar que no hay pines antes de crear clusters
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
      this.map.removeLayer(this.markersGroup);
      this.markersGroup = null;
    }
    
    // Crear grupo de clustering con configuración mejorada
    this.markerClusterGroup = (L as any).markerClusterGroup({
      // Desactivar spiderfy y en su lugar hacer zoom
      spiderfyOnMaxZoom: false,
      showCoverageOnHover: false,
      zoomToBoundsOnClick: true,
      disableClusteringAtZoom: 18, 
      maxClusterRadius: 50,
      animate: true,
      animateAddingMarkers: true,
      spiderfyDistanceMultiplier: 1,
      // Función personalizada para manejar clicks en clusters
      iconCreateFunction: function(cluster: any) {
        const count = cluster.getChildCount();
        return new L.DivIcon({
          html: `<div><span>${count}</span></div>`,
          className: 'marker-cluster marker-cluster-medium',
          iconSize: new L.Point(40, 40)
        });
      }
    });

    // Agregar evento personalizado para el click en clusters
    this.markerClusterGroup.on('clusterclick', (e: any) => {
      const cluster = e.layer;
      const children = cluster.getAllChildMarkers();
      
      // Si hay pocos marcadores, mostrar popup con información
      if (children.length <= 5) {
        let popupContent = `<div><strong>Ubicaciones agrupadas (${children.length}):</strong><br>`;
        children.forEach((marker: any, index: number) => {
          const popup = marker.getPopup();
          if (popup) {
            popupContent += `<small>${index + 1}. ${popup.getContent()}</small><br>`;
          }
        });
        popupContent += '</div>';
        
        L.popup()
          .setLatLng(cluster.getLatLng())
          .setContent(popupContent)
          .openOn(this.map);
      } else {
        // Si hay muchos marcadores, hacer zoom hacia la zona
        this.map.setView(cluster.getLatLng(), Math.min(this.map.getZoom() + 2, 18));
      }
    });

    this.markerClusterGroup.addTo(this.map);

    let marcadoresAgregados = 0;
    this.coordenadas.forEach(coordenada => {
      if (this.validarCoordenadas(coordenada.latitud, coordenada.longitud)) {
        const marker = L.marker([coordenada.latitud, coordenada.longitud])
          .bindPopup(`
            <div>
              <strong>Encuesta ID:</strong> ${coordenada.encuestaId}<br>
              <strong>Latitud:</strong> ${coordenada.latitud}<br>
              <strong>Longitud:</strong> ${coordenada.longitud}
            </div>
          `);
        
        this.markerClusterGroup.addLayer(marker);
        marcadoresAgregados++;
      }
    });

    console.log(`${marcadoresAgregados} clusters agregados al mapa`);
    this.ajustarVistaAMarcadores();
  }

  private async mostrarHeatmap(): Promise<void> {
    const L = await import('leaflet');
    
    // Asegurar que no hay pines ni clusters antes de crear heatmap
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
      this.map.removeLayer(this.markersGroup);
      this.markersGroup = null;
    }
    
    if (this.markerClusterGroup) {
      this.markerClusterGroup.clearLayers();
      this.map.removeLayer(this.markerClusterGroup);
      this.markerClusterGroup = null;
    }
    
    // Preparar datos para el heatmap: [lat, lng, intensity]
    const heatmapData = this.coordenadas
      .filter(coordenada => this.validarCoordenadas(coordenada.latitud, coordenada.longitud))
      .map(coordenada => [coordenada.latitud, coordenada.longitud, 1.0]); // intensidad fija de 1.0
    
    if (heatmapData.length > 0) {
      // Crear el heatmap layer con configuraciones optimizadas
      this.heatmapLayer = (L as any).heatLayer(heatmapData, {
        radius: 25,
        blur: 15,
        maxZoom: 17,
        max: 1.0,
        gradient: {
          0.4: 'blue',
          0.65: 'lime', 
          1: 'red'
        }
      }).addTo(this.map);
      
      console.log(`Heatmap con ${heatmapData.length} puntos agregado al mapa`);
      this.ajustarVistaAMarcadores();
    } else {
      console.log('No hay datos válidos para el heatmap');
    }
  }

  private limpiarMarcadores(): void {
    // Limpiar marcadores de pines
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
      this.map.removeLayer(this.markersGroup);
      this.markersGroup = null;
    }
    
    // Limpiar marcadores de clusters
    if (this.markerClusterGroup) {
      this.markerClusterGroup.clearLayers();
      this.map.removeLayer(this.markerClusterGroup);
      this.markerClusterGroup = null;
    }
    
    // Limpiar heatmap
    if (this.heatmapLayer) {
      this.map.removeLayer(this.heatmapLayer);
      this.heatmapLayer = null;
    }
  }

  private async ajustarVistaAMarcadores(): Promise<void> {
    if (this.coordenadas.length > 0) {
      const coordenadasValidas = this.coordenadas.filter(c => 
        this.validarCoordenadas(c.latitud, c.longitud)
      );
      
      if (coordenadasValidas.length > 0) {
        const L = await import('leaflet');
        const bounds = L.latLngBounds(coordenadasValidas.map(c => [c.latitud, c.longitud]));
        this.map.fitBounds(bounds.pad(0.1));
      }
    }
  }

  private async agregarMarcadoresFiltrados(respuestaValor: string): Promise<void> {
    if (!this.map || this.coordenadas.length === 0) return;

    // Limpiar marcadores existentes ANTES de agregar filtrados
    this.limpiarMarcadores();
    
    // Mostrar según el tipo seleccionado para datos filtrados
    if (this.tipoMarcador() === 'clusters') {
      await this.mostrarClustersFiltrados(respuestaValor);
    } else if (this.tipoMarcador() === 'heatmap') {
      await this.mostrarHeatmapFiltrado(respuestaValor);
    } else {
      await this.mostrarPinesFiltrados(respuestaValor);
    }
  }

  private async mostrarPinesFiltrados(respuestaValor: string): Promise<void> {
    const L = await import('leaflet');
    
    // Asegurar que no hay clusters
    if (this.markerClusterGroup) {
      this.markerClusterGroup.clearLayers();
      this.map.removeLayer(this.markerClusterGroup);
      this.markerClusterGroup = null;
    }
    
    this.markersGroup = L.layerGroup().addTo(this.map);

    // Agrupar coordenadas por ubicación exacta para aplicar jitter
    const coordenadasPorUbicacion = new Map<string, any[]>();
    
    this.coordenadas.forEach(coordenada => {
      if (this.validarCoordenadas(coordenada.latitud, coordenada.longitud)) {
        const key = `${coordenada.latitud.toFixed(6)},${coordenada.longitud.toFixed(6)}`;
        if (!coordenadasPorUbicacion.has(key)) {
          coordenadasPorUbicacion.set(key, []);
        }
        coordenadasPorUbicacion.get(key)!.push(coordenada);
      }
    });

    // Agregar jitter (dispersión) a coordenadas duplicadas y crear marcadores
    let marcadoresAgregados = 0;
    coordenadasPorUbicacion.forEach((encuestas, ubicacion) => {
      const [lat, lng] = ubicacion.split(',').map(Number);
      
      if (encuestas.length === 1) {
        // Marcador único - sin jitter
        const marker = L.marker([lat, lng])
          .bindPopup(`
            <div>
              <strong>Encuesta ID:</strong> ${encuestas[0].encuestaId}<br>
              <strong>Respuesta:</strong> ${respuestaValor}<br>
              <strong>Latitud:</strong> ${lat.toFixed(6)}<br>
              <strong>Longitud:</strong> ${lng.toFixed(6)}
            </div>
          `);
        this.markersGroup.addLayer(marker);
        marcadoresAgregados++;
      } else {
        // Múltiples encuestas en la misma ubicación - aplicar jitter
        encuestas.forEach((encuesta, index) => {
          const jitterRange = 0.0001; // Aproximadamente 11 metros
          const jitterLat = lat + (Math.random() - 0.5) * jitterRange;
          const jitterLng = lng + (Math.random() - 0.5) * jitterRange;
          
          const marker = L.marker([jitterLat, jitterLng])
            .bindPopup(`
              <div>
                <strong>Encuesta ID:</strong> ${encuesta.encuestaId}<br>
                <strong>Respuesta:</strong> ${respuestaValor}<br>
                <strong>Ubicación:</strong> ${encuestas.length} encuesta(s) en esta área<br>
                <strong>Latitud original:</strong> ${lat.toFixed(6)}<br>
                <strong>Longitud original:</strong> ${lng.toFixed(6)}
              </div>
            `);
          this.markersGroup.addLayer(marker);
          marcadoresAgregados++;
        });
      }
    });

    console.log(`${marcadoresAgregados} marcadores filtrados agregados al mapa`);
    this.ajustarVistaAMarcadores();
  }

  private async mostrarClustersFiltrados(respuestaValor: string): Promise<void> {
    const L = await import('leaflet');
    
    // Asegurar que no hay pines
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
      this.map.removeLayer(this.markersGroup);
      this.markersGroup = null;
    }
    
    // Crear grupo de clustering con la misma configuración mejorada
    this.markerClusterGroup = (L as any).markerClusterGroup({
      spiderfyOnMaxZoom: false,
      showCoverageOnHover: false,
      zoomToBoundsOnClick: true,
      disableClusteringAtZoom: 18,
      maxClusterRadius: 80,
      spiderfyDistanceMultiplier: 1,
      iconCreateFunction: function(cluster: any) {
        const count = cluster.getChildCount();
        return new L.DivIcon({
          html: `<div><span>${count}</span></div>`,
          className: 'marker-cluster marker-cluster-medium',
          iconSize: new L.Point(40, 40)
        });
      }
    });

    // Agregar el mismo evento personalizado para clusters filtrados
    this.markerClusterGroup.on('clusterclick', (e: any) => {
      const cluster = e.layer;
      const children = cluster.getAllChildMarkers();
      
      if (children.length <= 5) {
        let popupContent = `<div><strong>Ubicaciones filtradas (${children.length}):</strong><br>`;
        popupContent += `<strong>Respuesta:</strong> ${respuestaValor}<br><br>`;
        children.forEach((marker: any, index: number) => {
          const popup = marker.getPopup();
          if (popup) {
            const content = popup.getContent();
            // Extraer solo la información de la encuesta
            const encuestaMatch = content.match(/Encuesta ID:<\/strong>\s*(\d+)/);
            const encuestaId = encuestaMatch ? encuestaMatch[1] : 'N/A';
            popupContent += `<small>${index + 1}. Encuesta ID: ${encuestaId}</small><br>`;
          }
        });
        popupContent += '</div>';
        
        L.popup()
          .setLatLng(cluster.getLatLng())
          .setContent(popupContent)
          .openOn(this.map);
      } else {
        this.map.setView(cluster.getLatLng(), Math.min(this.map.getZoom() + 2, 18));
      }
    });

    this.markerClusterGroup.addTo(this.map);

    this.coordenadas.forEach(coordenada => {
      if (this.validarCoordenadas(coordenada.latitud, coordenada.longitud)) {
        const marker = L.marker([coordenada.latitud, coordenada.longitud])
          .bindPopup(`
            <div>
              <strong>Encuesta ID:</strong> ${coordenada.encuestaId}<br>
              <strong>Respuesta:</strong> ${respuestaValor}<br>
              <strong>Latitud:</strong> ${coordenada.latitud}<br>
              <strong>Longitud:</strong> ${coordenada.longitud}
            </div>
          `);
        
        this.markerClusterGroup.addLayer(marker);
      }
    });

    this.ajustarVistaAMarcadores();

    console.log(`Marcadores filtrados agregados como clusters: ${this.coordenadas.length} para respuesta "${respuestaValor}"`);
  }

  private async mostrarHeatmapFiltrado(respuestaValor: string): Promise<void> {
    const L = await import('leaflet');
    
    // Asegurar que no hay pines ni clusters antes de crear heatmap
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
      this.map.removeLayer(this.markersGroup);
      this.markersGroup = null;
    }
    
    if (this.markerClusterGroup) {
      this.markerClusterGroup.clearLayers();
      this.map.removeLayer(this.markerClusterGroup);
      this.markerClusterGroup = null;
    }
    
    // Preparar datos para el heatmap filtrado: [lat, lng, intensity]
    const heatmapData = this.coordenadas
      .filter(coordenada => this.validarCoordenadas(coordenada.latitud, coordenada.longitud))
      .map(coordenada => [coordenada.latitud, coordenada.longitud, 1.0]); // intensidad fija de 1.0
    
    if (heatmapData.length > 0) {
      // Crear el heatmap layer con configuraciones optimizadas para datos filtrados
      this.heatmapLayer = (L as any).heatLayer(heatmapData, {
        radius: 30, // Radio ligeramente mayor para datos filtrados
        blur: 20,
        maxZoom: 17,
        max: 1.0,
        gradient: {
          0.4: 'blue',
          0.65: 'lime', 
          1: 'red'
        }
      }).addTo(this.map);
      
      this.ajustarVistaAMarcadores();
      
      console.log(`Heatmap filtrado creado: ${heatmapData.length} puntos para respuesta "${respuestaValor}"`);
    }
  }

  private validarCoordenadas(lat: number, lng: number): boolean {
    return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
  }

  // === MÉTODOS DE FILTROS MÚLTIPLES (copiados de estadísticas) ===

  cargarTodasLasCategorias() {
    this.estadisticasService.filtrarPreguntasRespuestas({}).subscribe(data => {
      this.categorias = Array.from(new Set(data.map(pr => pr.categoria).filter(Boolean)));
      this.todasLasRespuestas = data;
      
      // Forzar detección de cambios
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 0);
    });
  }

  onCategoriaChange() {
    if (this.nuevaCategoria) {
      this.estadisticasService.getPreguntasPorCategoria(this.nuevaCategoria).subscribe(preguntas => {
        this.preguntasPorCategoria = preguntas;
        this.nuevaPregunta = '';
        this.respuestasPorPregunta = [];
        this.nuevaRespuesta = '';
        this.cdr.detectChanges();
      });
    } else {
      this.preguntasPorCategoria = [];
      this.respuestasPorPregunta = [];
      this.nuevaPregunta = '';
      this.nuevaRespuesta = '';
    }
  }

  onPreguntaChange() {
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
      }
      this.cdr.detectChanges();
    } else {
      this.respuestasPorPregunta = [];
      this.cdr.detectChanges();
    }
  }

  agregarFiltro() {
    if (this.nuevaCategoria && this.nuevaPregunta && this.nuevaRespuesta) {
      // Verificar si el filtro ya existe
      const existe = this.filtrosMultiples.some(f => 
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
        
        this.cdr.detectChanges();
      }
    }
  }

  eliminarFiltro(index: number) {
    this.filtrosMultiples.splice(index, 1);
    this.cdr.detectChanges();
  }

  aplicarFiltros(): void {
    if (!this.tieneAlgunFiltroActivo()) {
      return;
    }

    this.cargandoFiltros = true;

    // Crear filtros para enviar al backend (igual que en estadísticas)
    const filtrosParaBackend = { ...this.filtros };
    filtrosParaBackend.filtrosMultiples = this.filtrosMultiples;

    // Usar el servicio de estadísticas para obtener los datos filtrados
    this.estadisticasService.filtrarPreguntasRespuestas(filtrosParaBackend).subscribe({
      next: (data) => {
        console.log('Datos filtrados recibidos para mapa:', data);
        
        // Procesar los datos para extraer coordenadas
        this.procesarDatosFiltrados(data);
        
        this.cargandoFiltros = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al aplicar filtros:', error);
        this.cargandoFiltros = false;
        this.cdr.detectChanges();
      }
    });
  }

  private procesarDatosFiltrados(data: any[]) {
    // Extraer encuestaIds únicos según la lógica de estadísticas
    const hayFiltrosMultiples = this.filtrosMultiples.length > 0;
    
    if (!hayFiltrosMultiples) {
      return;
    }
    
    const todosSonEspecificos = this.filtrosMultiples.every(f => 
      f.categoria !== 'TODAS' && f.pregunta !== 'TODAS' && f.respuesta !== 'TODAS'
    );
    
    let encuestaIdsUnicos: Set<number>;
    
    if (todosSonEspecificos) {
      // CASO 1: Filtros específicos - obtener encuestaIds únicos
      encuestaIdsUnicos = new Set();
      data.forEach(item => {
        if (item.encuestaId) {
          encuestaIdsUnicos.add(item.encuestaId);
        }
      });
    } else {
      // CASO 2 y 3: Mostrar todas las coordenadas de los datos filtrados
      encuestaIdsUnicos = new Set();
      data.forEach(item => {
        if (item.encuestaId) {
          encuestaIdsUnicos.add(item.encuestaId);
        }
      });
    }

    console.log('EncuestaIds únicos para mostrar en mapa:', Array.from(encuestaIdsUnicos));
    
    // Cargar coordenadas solo para estos encuestaIds
    this.cargarCoordenadasPorEncuestaIds(Array.from(encuestaIdsUnicos));
  }

  private cargarCoordenadasPorEncuestaIds(encuestaIds: number[]) {
    console.log('=== ANÁLISIS DE FILTRADO DE COORDENADAS ===');
    console.log('EncuestaIds solicitados:', encuestaIds.length, encuestaIds.slice(0, 10));
    
    // Obtener todas las coordenadas y filtrar por los encuestaIds
    this.mapaService.obtenerCoordenadasMapa().subscribe({
      next: (coordenadasDTO) => {
        console.log('Coordenadas crudas del backend:', coordenadasDTO.length);
        
        // Analizar encuestaIds en las coordenadas crudas
        const encuestaIdsEnCoordenadas = coordenadasDTO.map(c => c.encuestaId);
        const encuestaIdsUnicos = [...new Set(encuestaIdsEnCoordenadas)];
        console.log('EncuestaIds únicos en coordenadas crudas:', encuestaIdsUnicos.length, encuestaIdsUnicos.slice(0, 10));
        
        const todasLasCoordenadas = this.mapaService.procesarCoordenadas(coordenadasDTO);
        console.log('Coordenadas procesadas:', todasLasCoordenadas.length);
        
        // Analizar encuestaIds en las coordenadas procesadas
        const encuestaIdsProcesados = todasLasCoordenadas.map(c => c.encuestaId);
        const encuestaIdsUnicosProcesados = [...new Set(encuestaIdsProcesados)];
        console.log('EncuestaIds únicos procesados:', encuestaIdsUnicosProcesados.length, encuestaIdsUnicosProcesados.slice(0, 10));
        
        // Filtrar solo las coordenadas de las encuestas que nos interesan
        this.coordenadas = todasLasCoordenadas.filter(coord => 
          encuestaIds.includes(coord.encuestaId)
        );
        
        // Analizar el match
        const encuestaIdsEncontrados = this.coordenadas.map(c => c.encuestaId);
        const encuestaIdsUnicosEncontrados = [...new Set(encuestaIdsEncontrados)];
        console.log('EncuestaIds que hicieron match:', encuestaIdsUnicosEncontrados.length, encuestaIdsUnicosEncontrados.slice(0, 10));
        
        // Verificar qué encuestaIds no se encontraron
        const noEncontrados = encuestaIds.filter(id => !encuestaIdsUnicosEncontrados.includes(id));
        console.log('EncuestaIds solicitados pero NO encontrados:', noEncontrados.length, noEncontrados);
        
        // Actualizar signals con la información de encuestas sin coordenadas
        this.totalEncuestasFiltradas.set(encuestaIds.length);
        this.encuestasSinCoordenadas.set(noEncontrados.length);
        
        // Verificar si los no encontrados existen en las coordenadas crudas
        const noEncontradosEnCrudas = noEncontrados.filter(id => encuestaIdsUnicos.includes(id));
        console.log('De los no encontrados, cuántos SÍ están en coordenadas crudas:', noEncontradosEnCrudas.length, noEncontradosEnCrudas);
        
        console.log(`RESULTADO: ${this.coordenadas.length} coordenadas filtradas de ${todasLasCoordenadas.length} totales`);
        
        // DEBUG: Verificar coordenadas duplicadas
        const coordenadasUnicas = new Map();
        this.coordenadas.forEach(coord => {
          const key = `${coord.latitud},${coord.longitud}`;
          if (coordenadasUnicas.has(key)) {
            coordenadasUnicas.get(key).push(coord.encuestaId);
          } else {
            coordenadasUnicas.set(key, [coord.encuestaId]);
          }
        });
        
        const ubicacionesDuplicadas = Array.from(coordenadasUnicas.entries()).filter(([key, encuestaIds]) => encuestaIds.length > 1);
        console.log(`Ubicaciones únicas: ${coordenadasUnicas.size} de ${this.coordenadas.length} coordenadas`);
        if (ubicacionesDuplicadas.length > 0) {
          console.log('Ubicaciones con múltiples encuestas:', ubicacionesDuplicadas);
        }
        
        console.log('=== FIN ANÁLISIS ===');
        
        // Actualizar el mapa
        if (this.map) {
          // Aplicar jitter para coordenadas duplicadas
          this.aplicarJitterACoordenadasDuplicadas();
          this.agregarMarcadores();
        } else {
          console.error('El mapa no está inicializado');
        }
      },
      error: (error) => {
        console.error('Error al cargar coordenadas filtradas:', error);
      }
    });
  }

  limpiarTodosFiltros() {
    this.filtrosMultiples = [];
    this.nuevaCategoria = '';
    this.nuevaPregunta = '';
    this.nuevaRespuesta = '';
    this.preguntasPorCategoria = [];
    this.respuestasPorPregunta = [];
    
    // Recargar todas las coordenadas
    this.cargarYMostrarCoordenadas();
    
    this.cdr.detectChanges();
  }

  limpiarRangoEdad() {
    this.filtros.edadDesde = undefined;
    this.filtros.edadHasta = undefined;
    
    if (this.filtrosMultiples.length > 0) {
      this.aplicarFiltros();
    }
    
    this.cdr.detectChanges();
  }

  tieneAlgunFiltroActivo(): boolean {
    return this.filtrosMultiples.length > 0;
  }

  trackByIndex(index: number, item: any): any {
    return index;
  }

  // Aplicar pequeño desplazamiento a coordenadas duplicadas para que sean visibles en el mapa
  private aplicarJitterACoordenadasDuplicadas(): void {
    const coordenadasPorUbicacion = new Map<string, any[]>();
    
    // Agrupar coordenadas por ubicación
    this.coordenadas.forEach(coord => {
      const key = `${coord.latitud},${coord.longitud}`;
      if (!coordenadasPorUbicacion.has(key)) {
        coordenadasPorUbicacion.set(key, []);
      }
      coordenadasPorUbicacion.get(key)!.push(coord);
    });
    
    // Aplicar jitter solo a ubicaciones con múltiples encuestas
    coordenadasPorUbicacion.forEach((coords, ubicacion) => {
      if (coords.length > 1) {
        coords.forEach((coord, index) => {
          if (index > 0) { // Mantener la primera sin cambios
            // Desplazamiento muy pequeño (aproximadamente 5-10 metros)
            const jitterLat = (Math.random() - 0.5) * 0.0001; // ~11 metros
            const jitterLng = (Math.random() - 0.5) * 0.0001; // ~11 metros
            
            coord.latitud += jitterLat;
            coord.longitud += jitterLng;
          }
        });
        console.log(`Aplicado jitter a ${coords.length} encuestas en ubicación ${ubicacion}`);
      }
    });
  }
}