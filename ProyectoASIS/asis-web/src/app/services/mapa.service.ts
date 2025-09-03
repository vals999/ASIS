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

  verificarSiExistenDatos(): Observable<{existenDatos: boolean, totalRespuestas: number}> {
    return this.http.get<{existenDatos: boolean, totalRespuestas: number}>(`${this.apiUrl}/respuestas-encuesta/existe-datos`);
  }

  obtenerPreguntasHardcodeadas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/preguntas-encuesta/preguntas-hardcodeadas`);
  }

  obtenerRespuestasUnicasPorPregunta(preguntaCodigo: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/respuestas-encuesta/respuestas-unicas/${preguntaCodigo}`);
  }

  obtenerCoordenadasFiltradas(preguntaCodigo: string, respuestaValor: string): Observable<CoordenadaMapaDTO[]> {
    const params = new URLSearchParams();
    params.set('preguntaCodigo', preguntaCodigo);
    params.set('respuestaValor', respuestaValor);
    
    return this.http.get<CoordenadaMapaDTO[]>(`${this.apiUrl}/respuestas-encuesta/coordenadas-filtradas?${params.toString()}`);
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