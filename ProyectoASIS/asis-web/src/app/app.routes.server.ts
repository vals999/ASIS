import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Páginas públicas que SÍ deben prerenderizarse
  {
    path: '',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'landing',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'login',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'register',
    renderMode: RenderMode.Prerender
  },
  // Todas las demás rutas (autenticadas) deben renderizarse en el servidor
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
