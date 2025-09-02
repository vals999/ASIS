import { Component, OnInit, AfterViewInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MapaService, Coordenada } from '../../services/mapa.service';
import { CargaArchivosComponent } from '../carga-archivos/carga-archivos';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule, CargaArchivosComponent],
  templateUrl: './mapa.html',
  styleUrl: './mapa.css'
})
export class MapaComponent implements OnInit, AfterViewInit {
  private map: any;
  public isBrowser: boolean;
  private coordenadas: Coordenada[] = [];
  public cargandoDatos = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private mapaService: MapaService
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      this.cargarCoordenadas();
    }
  }

  ngAfterViewInit(): void {
    if (this.isBrowser) {
      this.loadLeafletAndInitMap();
    }
  }

  private cargarCoordenadas(): void {
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
    
    // Crear un grupo de marcadores para mejor gestión
    const markersGroup = L.layerGroup().addTo(this.map);

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
        
        markersGroup.addLayer(marker);
      }
    });

    // Ajustar la vista del mapa para mostrar todos los marcadores
    if (this.coordenadas.length > 0) {
      const coordenadasValidas = this.coordenadas.filter(c => 
        this.validarCoordenadas(c.latitud, c.longitud)
      );
      
      if (coordenadasValidas.length > 0) {
        const group = new L.FeatureGroup(markersGroup.getLayers());
        this.map.fitBounds(group.getBounds().pad(0.1));
      }
    }
  }

  private validarCoordenadas(lat: number, lng: number): boolean {
    return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
  }
}