import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-coordenadas-selector',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal fade show" style="display: block; background-color: rgba(0,0,0,0.5);" 
         (click)="cerrarModal()">
      <div class="modal-dialog modal-lg" (click)="$event.stopPropagation()">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-geo-alt me-2"></i>
              Seleccionar Coordenadas
            </h5>
            <button type="button" class="btn-close" (click)="cerrarModal()"></button>
          </div>
          <div class="modal-body p-0">
            <div id="mapSelector" style="height: 400px; width: 100%;"></div>
            @if (coordenadasSeleccionadas) {
              <div class="p-3 bg-light border-top">
                <small class="text-muted">Coordenadas seleccionadas:</small>
                <div class="fw-bold">{{ coordenadasSeleccionadas }}</div>
              </div>
            }
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="cerrarModal()">
              Cancelar
            </button>
            <button type="button" class="btn btn-primary" 
                    [disabled]="!coordenadasSeleccionadas"
                    (click)="confirmarSeleccion()">
              <i class="bi bi-check me-1"></i>
              Confirmar Coordenadas
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal {
      z-index: 1050;
    }
    
    .modal-dialog {
      margin: 1.75rem auto;
    }
    
    #mapSelector {
      border-radius: 0;
    }
    
    .btn-close {
      background: none;
      border: none;
      font-size: 1.5rem;
      font-weight: 700;
      line-height: 1;
      color: #000;
      text-shadow: 0 1px 0 #fff;
      opacity: 0.5;
      cursor: pointer;
    }
    
    .btn-close:hover {
      opacity: 0.75;
    }
  `]
})
export class CoordenadasSelectorComponent implements OnInit, OnDestroy {
  @Input() coordenadasIniciales?: string;
  @Output() coordenadasSelected = new EventEmitter<string>();
  @Output() modalClosed = new EventEmitter<void>();

  private map: any;
  private marker: any;
  public isBrowser: boolean;
  public coordenadasSeleccionadas?: string;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      // Pequeño delay para asegurar que el DOM esté listo
      setTimeout(() => {
        this.loadLeafletAndInitMap();
      }, 100);
      
      // Si hay coordenadas iniciales, establecerlas
      if (this.coordenadasIniciales && this.coordenadasIniciales.trim()) {
        this.coordenadasSeleccionadas = this.coordenadasIniciales;
      }
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  private async loadLeafletAndInitMap(): Promise<void> {
    try {
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
    // Coordenadas por defecto - La Plata, Argentina
    let initialLat = -34.9215;
    let initialLng = -57.9545;
    let initialZoom = 13;

    // Si hay coordenadas iniciales, usarlas
    if (this.coordenadasIniciales && this.coordenadasIniciales.includes(',')) {
      const [lat, lng] = this.coordenadasIniciales.split(',').map(coord => parseFloat(coord.trim()));
      if (!isNaN(lat) && !isNaN(lng)) {
        initialLat = lat;
        initialLng = lng;
        initialZoom = 15;
      }
    }

    // Inicializar el mapa
    this.map = L.map('mapSelector').setView([initialLat, initialLng], initialZoom);

    // Agregar capa de tiles de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    // Si hay coordenadas iniciales, agregar marcador
    if (this.coordenadasIniciales && this.coordenadasIniciales.includes(',')) {
      const [lat, lng] = this.coordenadasIniciales.split(',').map(coord => parseFloat(coord.trim()));
      if (!isNaN(lat) && !isNaN(lng)) {
        this.marker = L.marker([lat, lng]).addTo(this.map);
      }
    }

    // Agregar evento de clic al mapa
    this.map.on('click', (e: any) => {
      const lat = e.latlng.lat;
      const lng = e.latlng.lng;
      
      // Formatear coordenadas con 6 decimales
      const coordenadas = `${lat.toFixed(6)}, ${lng.toFixed(6)}`;
      this.coordenadasSeleccionadas = coordenadas;

      // Remover marcador anterior si existe
      if (this.marker) {
        this.map.removeLayer(this.marker);
      }

      // Agregar nuevo marcador
      this.marker = L.marker([lat, lng])
        .addTo(this.map)
        .bindPopup(`Coordenadas: ${coordenadas}`)
        .openPopup();
    });

    // Forzar redimensionamiento después de un pequeño delay
    setTimeout(() => {
      this.map.invalidateSize();
    }, 100);
  }

  cerrarModal(): void {
    this.modalClosed.emit();
  }

  confirmarSeleccion(): void {
    if (this.coordenadasSeleccionadas) {
      this.coordenadasSelected.emit(this.coordenadasSeleccionadas);
      this.cerrarModal();
    }
  }
}