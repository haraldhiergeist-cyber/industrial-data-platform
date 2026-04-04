import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';

export default [
  { path: '', component: DashboardComponent },
  {
    path: 'temperature-history',
    loadComponent: () =>
      import('./temperature-history/temperature-history.component').then(
        m => m.TemperatureHistoryComponent
      )
  },
  { path: '**', redirectTo: '/notfound' }
] as Routes;