import { Routes } from '@angular/router';
import { UsuarioComponent } from './components/usuario/usuario';
import { CampaniaComponent } from './components/campania/campania';
import { JornadaComponent } from './components/jornada/jornada';
import { BarrioComponent } from './components/barrio/barrio';
import { ZonaComponent } from './components/zona/zona';
import { EncuestadorComponent } from './components/encuestador/encuestador';

export const routes: Routes = [
  { path: 'usuarios', component: UsuarioComponent },
  { path: 'campanias', component: CampaniaComponent },
  { path: 'jornadas', component: JornadaComponent },
  { path: 'barrios', component: BarrioComponent },
  { path: 'zonas', component: ZonaComponent },
  { path: 'encuestadores', component: EncuestadorComponent },
  { path: '', redirectTo: 'usuarios', pathMatch: 'full' }
];
