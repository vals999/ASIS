import { Component, OnInit, AfterViewInit, Inject, PLATFORM_ID, OnDestroy, signal } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MapaService, Coordenada } from '../../services/mapa.service';
import { CargaArchivosComponent } from '../carga-archivos/carga-archivos';
import { EventService } from '../../services/event.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule, CargaArchivosComponent],
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
  private showFilteredMapSubscription?: Subscription;
  private markersGroup: any;
  private markerClusterGroup: any;
  public csvCargado = false;
  
  // Signal para controlar si hay datos disponibles (como en carga-archivos)
  public datosDisponibles = signal<boolean>(false);
  
  // Signal para controlar el tipo de marcador (pines o clusters)
  public tipoMarcador = signal<'pines' | 'clusters'>('pines');

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private mapaService: MapaService,
    private eventService: EventService
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      // Verificar si ya existen datos al cargar el componente
      this.verificarDatosExistentes();
      
      // Suscribirse al evento de carga de CSV (solo para saber que se cargó)
      this.csvUploadSubscription = this.eventService.csvUploaded$.subscribe(() => {
        console.log('CSV cargado, esperando instrucción para mostrar mapa...');
        this.csvCargado = true;
        this.datosDisponibles.set(true);
      });

      // Suscribirse al evento para mostrar el mapa
      this.showMapSubscription = this.eventService.showMap$.subscribe(() => {
        console.log('Solicitado mostrar coordenadas en el mapa...');
        this.cargarYMostrarCoordenadas();
      });

      // Suscribirse al evento para mostrar coordenadas filtradas
      this.showFilteredMapSubscription = this.eventService.showFilteredMap$.subscribe((filtro) => {
        console.log(`Solicitado mostrar coordenadas filtradas - Pregunta: ${filtro.preguntaCodigo}, Respuesta: ${filtro.respuestaValor}`);
        this.cargarYMostrarCoordenadasFiltradas(filtro.preguntaCodigo, filtro.respuestaValor);
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
    if (this.showFilteredMapSubscription) {
      this.showFilteredMapSubscription.unsubscribe();
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

  cambiarTipoMarcador(tipo: 'pines' | 'clusters'): void {
    this.tipoMarcador.set(tipo);
    console.log(`Cambiando tipo de marcador a: ${tipo}`);
    
    // Si hay coordenadas cargadas, actualizar la visualización
    if (this.coordenadas.length > 0 && this.map) {
      // Limpiar todo antes de mostrar el nuevo tipo
      this.limpiarMarcadores();
      
      // Mostrar el nuevo tipo
      if (tipo === 'clusters') {
        this.mostrarClusters();
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

  private cargarYMostrarCoordenadasFiltradas(preguntaCodigo: string, respuestaValor: string): void {
    this.cargandoDatos = true;
    this.mapaService.obtenerCoordenadasFiltradas(preguntaCodigo, respuestaValor).subscribe({
      next: (coordenadasDTO) => {
        this.coordenadas = this.mapaService.procesarCoordenadas(coordenadasDTO);
        console.log('Coordenadas filtradas procesadas:', this.coordenadas);
        this.cargandoDatos = false;
        
        // Si el mapa ya está inicializado, agregar los marcadores filtrados
        if (this.map) {
          this.agregarMarcadoresFiltrados(respuestaValor);
        }
      },
      error: (error) => {
        console.error('Error al cargar coordenadas filtradas:', error);
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
    if (!this.map || this.coordenadas.length === 0) return;

    // Limpiar marcadores existentes ANTES de agregar nuevos
    this.limpiarMarcadores();
    
    // Mostrar según el tipo seleccionado
    if (this.tipoMarcador() === 'clusters') {
      await this.mostrarClusters();
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
        
        this.markersGroup.addLayer(marker);
      }
    });

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
      maxClusterRadius: 80,
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
      }
    });

    this.ajustarVistaAMarcadores();
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
        
        this.markersGroup.addLayer(marker);
      }
    });

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

  private validarCoordenadas(lat: number, lng: number): boolean {
    return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
  }
}