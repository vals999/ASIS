import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CoordenadaMapaDTO {
  encuestaId: number;
  respuestaId: number;
  valor: string;
  preguntaId: number;
  textoPregunta: string;
}

export interface Coordenada {
  encuestaId: number;
  latitud: number;
  longitud: number;
}

@Injectable({
  providedIn: 'root'
})
export class MapaService {
  private apiUrl = 'http://localhost:8080/ProyectoASIS/api';

  constructor(private http: HttpClient) {}

  obtenerCoordenadasMapa(): Observable<CoordenadaMapaDTO[]> {
    return this.http.get<CoordenadaMapaDTO[]>(`${this.apiUrl}/respuestas-encuesta/coordenadas-mapa`);
  }

  procesarCoordenadas(coordenadasDTO: CoordenadaMapaDTO[]): Coordenada[] {
    const coordenadas: Coordenada[] = [];

    coordenadasDTO.forEach(coordenadaDTO => {
      const encuestaId = coordenadaDTO.encuestaId;
      const valor = coordenadaDTO.valor; // Formato "latitud,longitud"

      if (encuestaId && valor && valor.includes(',')) {
        const [latitudStr, longitudStr] = valor.split(',');
        const latitud = parseFloat(latitudStr);
        const longitud = parseFloat(longitudStr);

        if (!isNaN(latitud) && !isNaN(longitud)) {
          coordenadas.push({
            encuestaId,
            latitud,
            longitud
          });
        }
      }
    });

    return coordenadas;
  }
}