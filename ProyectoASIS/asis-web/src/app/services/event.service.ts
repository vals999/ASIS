import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  // Evento para notificar cuando se sube un CSV
  private csvUploadedSubject = new Subject<void>();
  public csvUploaded$ = this.csvUploadedSubject.asObservable();

  // Evento para mostrar todas las coordenadas en el mapa
  private showMapSubject = new Subject<void>();
  public showMap$ = this.showMapSubject.asObservable();

  // Evento para mostrar coordenadas filtradas en el mapa
  private showFilteredMapSubject = new Subject<{preguntaCodigo: string, respuestaValor: string}>();
  public showFilteredMap$ = this.showFilteredMapSubject.asObservable();

  notifyCsvUploaded(): void {
    this.csvUploadedSubject.next();
  }

  notifyShowMap(): void {
    this.showMapSubject.next();
  }

  notifyShowFilteredMap(preguntaCodigo: string, respuestaValor: string): void {
    this.showFilteredMapSubject.next({preguntaCodigo, respuestaValor});
  }
}