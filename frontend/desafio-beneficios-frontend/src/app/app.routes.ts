import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'beneficios' },

  {
    path: 'beneficios',
    loadComponent: () =>
      import('./beneficios/pages/beneficio-list/beneficio-list.component')
        .then(m => m.BeneficioListComponent),
  },
  {
    path: 'beneficios/novo',
    loadComponent: () =>
      import('./beneficios/pages/beneficio-form/beneficio-form.component')
        .then(m => m.BeneficioFormComponent),
  },
  {
    path: 'beneficios/:id/editar',
    loadComponent: () =>
      import('./beneficios/pages/beneficio-form/beneficio-form.component')
        .then(m => m.BeneficioFormComponent),
  },
  {
    path: 'beneficios/transfer',
    loadComponent: () =>
      import('./beneficios/pages/beneficio-transfer/beneficio-transfer.component')
        .then(m => m.BeneficioTransferComponent),
  },

  { path: '**', redirectTo: 'beneficios' },
];