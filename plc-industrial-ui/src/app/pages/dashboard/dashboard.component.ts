import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { TableModule } from 'primeng/table';

import { PlcDashboardFacade } from '../service/plc-dashboard.facade';
import { PlcReadingEvent } from '../../api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  imports: [CommonModule, CardModule, TagModule, TableModule]
})
export class DashboardComponent implements OnInit, OnDestroy {
  readings: PlcReadingEvent[] = [];
  private readonly destroy$ = new Subject<void>();

  constructor(private dashboardFacade: PlcDashboardFacade) {}

  ngOnInit(): void {
    this.dashboardFacade.readings$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.readings = Array.isArray(data) ? data : [];
      });

    this.dashboardFacade.start(1000);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.dashboardFacade.stop();
  }

  getReading(tagName: string): PlcReadingEvent | undefined {
    return this.readings.find((r) => r.tagName === tagName);
  }

  getValue(tagName: string): string {
    return this.getReading(tagName)?.valueAsString ?? '-';
  }

  getQuality(tagName: string): string {
    return this.getReading(tagName)?.quality ?? 'BAD';
  }

  getSeverity(tagName: string): 'success' | 'danger' | 'warn' | 'info' | 'secondary' | 'contrast' {
    const quality = this.getReading(tagName)?.quality;
    return this.getSeverityFromQuality(quality);
  }

  getSeverityFromQuality(quality?: string): 'success' | 'danger' | 'warn' | 'info' | 'secondary' | 'contrast' {
    if (quality === 'GOOD') {
      return 'success';
    }

    if (quality === 'UNCERTAIN') {
      return 'warn';
    }

    return 'danger';
  }
}