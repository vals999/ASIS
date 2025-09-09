
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: true,
  baseHref: '/',
  locale: undefined,
  routes: [
  {
    "renderMode": 2,
    "route": "/"
  },
  {
    "renderMode": 2,
    "route": "/landing"
  },
  {
    "renderMode": 2,
    "route": "/login"
  },
  {
    "renderMode": 2,
    "route": "/register"
  },
  {
    "renderMode": 0,
    "route": "/inicio"
  },
  {
    "renderMode": 0,
    "route": "/perfil"
  },
  {
    "renderMode": 0,
    "route": "/usuarios"
  },
  {
    "renderMode": 0,
    "route": "/usuarios-pendientes"
  },
  {
    "renderMode": 0,
    "route": "/campanias"
  },
  {
    "renderMode": 0,
    "route": "/jornadas"
  },
  {
    "renderMode": 0,
    "route": "/barrios"
  },
  {
    "renderMode": 0,
    "route": "/zonas"
  },
  {
    "renderMode": 0,
    "route": "/encuestadores"
  },
  {
    "renderMode": 0,
    "route": "/mapa"
  },
  {
    "renderMode": 0,
    "route": "/reportes"
  },
  {
    "renderMode": 0,
    "route": "/estadisticas"
  },
  {
    "renderMode": 0,
    "redirectTo": "/",
    "route": "/**"
  }
],
  entryPointToBrowserMapping: undefined,
  assets: {
    'index.csr.html': {size: 6098, hash: '7249754b079bf9a027a0db6e7847ac38fb7c9fcefeeec25ae5cfc8c656904668', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 1856, hash: '27f8bb277740bdaa7acc155cea55268e63f4a0fcbd7d0964042a86a1c049de43', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'login/index.html': {size: 29235, hash: '21f75ff767c9a5ec322a1754aec0dcd8d023a1169351f4cbe386df8896c28c03', text: () => import('./assets-chunks/login_index_html.mjs').then(m => m.default)},
    'index.html': {size: 53299, hash: 'ee2b7cad06012043c46040f12b157e570fb049e41b402e1fb180c72acfbe7e6a', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'landing/index.html': {size: 53299, hash: 'ee2b7cad06012043c46040f12b157e570fb049e41b402e1fb180c72acfbe7e6a', text: () => import('./assets-chunks/landing_index_html.mjs').then(m => m.default)},
    'register/index.html': {size: 36371, hash: 'a92c7bb6316f5b68e93735b7888cb589b8353c73adaf30e0f3f5cd380a49aa41', text: () => import('./assets-chunks/register_index_html.mjs').then(m => m.default)},
    'styles-EODX7Z6R.css': {size: 231297, hash: 'fScEpIcnCqw', text: () => import('./assets-chunks/styles-EODX7Z6R_css.mjs').then(m => m.default)}
  },
};
