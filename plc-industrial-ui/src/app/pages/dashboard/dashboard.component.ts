import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { interval, Subject } from 'rxjs';
import { startWith, switchMap, takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { TableModule } from 'primeng/table';

import { PlcQueryControllerService, PlcReadingEvent } from '../../api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  imports: [CommonModule, CardModule, TagModule, TableModule]
})
export class DashboardComponent implements OnInit, OnDestroy {
  readings: PlcReadingEvent[] = [];
  private destroy$ = new Subject<void>();
  
  constructor(
    private plcApi: PlcQueryControllerService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    interval(1000)
      .pipe(
        startWith(0),
        takeUntil(this.destroy$),
        switchMap(() =>
          this.plcApi.getAll('body', false, { httpHeaderAccept: 'application/json' as any })
        )
      )
      .subscribe({
        next: (data) => {
          this.readings = Array.isArray(data) ? data : [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('PLC API error:', err);
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getReading(tagName: string): PlcReadingEvent | undefined {
    return this.readings.find(r => r.tagName === tagName);
  }

  getValue(tagName: string): string {
    return this.getReading(tagName)?.valueAsString ?? '-';
  }

  getQuality(tagName: string): string {
    return this.getReading(tagName)?.quality ?? 'BAD';
  }

  getSeverity(tagName: string): 'success' | 'danger' | 'warn' | 'info' | 'secondary' | 'contrast' {
    const quality = this.getReading(tagName)?.quality;
    if (quality === 'GOOD') return 'success';
    if (quality === 'UNCERTAIN') return 'warn';
    return 'danger';
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