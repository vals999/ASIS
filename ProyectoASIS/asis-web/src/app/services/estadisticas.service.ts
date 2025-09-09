// ...existing code...

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PreguntaRespuestaCategoria {
  pregunta: string;
  respuesta: string;
  categoria: string;
  encuestaId?: number;
  zona?: string;
  barrio?: string;
  campania?: string;
  sexo?: string;
  encuesta_id?: number;
  [key: string]: any; // Para propiedades adicionales
}

export interface FiltroMultiple {
  categoria: string;
  pregunta: string;
  respuesta: string;
}

export interface Filtros {
  categoria?: string;
  zona?: string;
  barrio?: string;
  campania?: string;
  fechaDesde?: string;
  fechaHasta?: string;
  sexo?: string;
  edadDesde?: number;
  edadHasta?: number;
  organizacionSocial?: string;
  tipoRespuesta?: string;
  perfil?: string;
  jornada?: string;
  encuestador?: string;
  // NUEVO: Filtros múltiples
  filtrosMultiples?: FiltroMultiple[];
}

@Injectable({ providedIn: 'root' })
export class EstadisticasService {
  getPreguntasPorCategoria(categoria: string): Observable<string[]> {
    const url = `/api/respuestas-encuesta/preguntas-por-categoria?categoria=${encodeURIComponent(categoria)}`;
    return this.http.get<string[]>(url);
  }
  private apiUrl = '/api/respuestas-encuesta/filtrar-preguntas-respuestas';

  constructor(private http: HttpClient) {}

  filtrarPreguntasRespuestas(filtros: Filtros): Observable<PreguntaRespuestaCategoria[]> {
    return this.http.post<PreguntaRespuestaCategoria[]>(this.apiUrl, filtros);
  }
}
