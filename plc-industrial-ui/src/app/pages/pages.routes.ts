import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';

export default [
  { path: '', component: DashboardComponent },
  {
    path: 'measurements',
    loadComponent: () =>
      import('./measurements-history/measurements.component').then(
        m => m.MeasurementsComponent
      )
  },
  {
    path: 'temperature-history',
    loadComponent: () =>
      import('./temperature-history/temperature-history.component').then(
        m => m.TemperatureHistoryComponent
      )
  },
  { path: '**', redirectTo: '/notfound' }
] as Routes;