import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface RespuestaEncuesta {
  id: number;
  preguntaId: number;
  valor: string;
}

export interface PreguntaEncuesta {
  id: number;
  texto?: string;
  preguntaCsv?: string;
  categoria: string;
  respuestaEncuesta: RespuestaEncuesta[];
}

export interface PreguntaRespuesta {
  pregunta: string;
  respuesta: string;
  categoria: string;
}

@Injectable({ providedIn: 'root' })
export class EstadisticasService {
  constructor(private http: HttpClient) {}

  getPreguntasRespuestas(): Observable<PreguntaRespuesta[]> {
    return this.http.get<PreguntaEncuesta[]>('/api/preguntas-encuesta').pipe(
      map((preguntas: PreguntaEncuesta[]) => {
        const resultado: PreguntaRespuesta[] = [];
        preguntas.forEach((pregunta) => {
          (pregunta.respuestaEncuesta || []).forEach((respuesta) => {
            resultado.push({
              pregunta: pregunta.texto || pregunta.preguntaCsv || '',
              respuesta: respuesta.valor,
              categoria: pregunta.categoria
            });
          });
        });
        return resultado;
      })
    );
  }
}
