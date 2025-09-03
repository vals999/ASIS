import { Component, OnInit, AfterViewInit, Inject, PLATFORM_ID, OnDestroy } from '@angular/core';
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
  public csvCargado = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private mapaService: MapaService,
    private eventService: EventService
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      // No cargar coordenadas automáticamente al inicio
      
      // Suscribirse al evento de carga de CSV (solo para saber que se cargó)
      this.csvUploadSubscription = this.eventService.csvUploaded$.subscribe(() => {
        console.log('CSV cargado, esperando instrucción para mostrar mapa...');
        this.csvCargado = true;
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

  // Método legacy mantenido para compatibilidad (ahora solo usado internamente)
  private cargarCoordenadas(): void {
    this.cargarYMostrarCoordenadas();
  }

  private actualizarMapa(): void {
    // Limpiar marcadores existentes
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
    }
    
    // Recargar coordenadas desde el servidor
    this.cargarYMostrarCoordenadas();
  }

  private async loadLeafletAndInitMap(): Promise<void> {
    try {
      // Importación dinámica de Leaflet solo en el navegador
      const L = await import('leaflet');
      
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

    const L = await import('leaflet');
    
    // Limpiar marcadores existentes antes de agregar nuevos
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
      this.map.removeLayer(this.markersGroup);
    }
    
    // Crear un nuevo grupo de marcadores
    this.markersGroup = L.layerGroup().addTo(this.map);

    this.coordenadas.forEach(coordenada => {
      // Validar que las coordenadas estén en un rango válido
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

    // Ajustar la vista del mapa para mostrar todos los marcadores
    if (this.coordenadas.length > 0) {
      const coordenadasValidas = this.coordenadas.filter(c => 
        this.validarCoordenadas(c.latitud, c.longitud)
      );
      
      if (coordenadasValidas.length > 0) {
        const group = new L.FeatureGroup(this.markersGroup.getLayers());
        this.map.fitBounds(group.getBounds().pad(0.1));
      }
    }
  }

  private async agregarMarcadoresFiltrados(respuestaValor: string): Promise<void> {
    if (!this.map || this.coordenadas.length === 0) return;

    const L = await import('leaflet');
    
    // Limpiar marcadores existentes antes de agregar nuevos
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
      this.map.removeLayer(this.markersGroup);
    }
    
    // Crear un nuevo grupo de marcadores
    this.markersGroup = L.layerGroup().addTo(this.map);

    this.coordenadas.forEach(coordenada => {
      // Validar que las coordenadas estén en un rango válido
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

    // Ajustar la vista del mapa para mostrar todos los marcadores filtrados
    if (this.coordenadas.length > 0) {
      const coordenadasValidas = this.coordenadas.filter(c => 
        this.validarCoordenadas(c.latitud, c.longitud)
      );
      
      if (coordenadasValidas.length > 0) {
        const group = new L.FeatureGroup(this.markersGroup.getLayers());
        this.map.fitBounds(group.getBounds().pad(0.1));
      }
    }

    console.log(`Marcadores filtrados agregados: ${this.coordenadas.length} para respuesta "${respuestaValor}"`);
  }

  private validarCoordenadas(lat: number, lng: number): boolean {
    return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
  }
}