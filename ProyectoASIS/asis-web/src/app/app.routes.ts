
import { Routes } from '@angular/router';
import { UsuarioComponent } from './components/usuario/usuario';
import { CampaniaComponent } from './components/campania/campania';
import { JornadaComponent } from './components/jornada/jornada';
import { BarrioComponent } from './components/barrio/barrio';
import { ZonaComponent } from './components/zona/zona';
import { EncuestadorComponent } from './components/encuestador/encuestador';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { UsuariosPendientesComponent } from './components/usuarios-pendientes/usuarios-pendientes';
import { MapaComponent } from './components/mapa/mapa';
import { ReportesComponent } from './components/reportes/reportes';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';
import { Inicio } from './components/inicio/inicio';
import { LandingComponent } from './components/landing/landing';
import { EstadisticasComponent } from './components/estadisticas/estadisticas';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'landing', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'inicio', component: Inicio, canActivate: [authGuard] },
  { path: 'usuarios', component: UsuarioComponent, canActivate: [authGuard] },
  { path: 'usuarios-pendientes', component: UsuariosPendientesComponent, canActivate: [authGuard, adminGuard] },
  { path: 'campanias', component: CampaniaComponent, canActivate: [authGuard] },
  { path: 'jornadas', component: JornadaComponent, canActivate: [authGuard] },
  { path: 'barrios', component: BarrioComponent, canActivate: [authGuard] },
  { path: 'zonas', component: ZonaComponent, canActivate: [authGuard] },
  { path: 'encuestadores', component: EncuestadorComponent, canActivate: [authGuard] },
  { path: 'mapa', component: MapaComponent, canActivate: [authGuard] },
  { path: 'reportes', component: ReportesComponent, canActivate: [authGuard] },
  { path: 'estadisticas', component: EstadisticasComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '/' }
];
