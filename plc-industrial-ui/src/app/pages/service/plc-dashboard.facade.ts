import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { PlcReadingEvent } from '../../api';
import { PlcPollingService } from './plc-polling.service';

@Injectable({
  providedIn: 'root'
})
export class PlcDashboardFacade {
  private stop$ = new Subject<void>();

  private readonly readingsSubject = new BehaviorSubject<PlcReadingEvent[]>([]);
  readonly readings$ = this.readingsSubject.asObservable();

  constructor(private pollingService: PlcPollingService) {}

  start(intervalMs = 1000): void {
    this.stopCurrentStream();

    this.pollingService.watchAll(intervalMs)
      .pipe(takeUntil(this.stop$))
      .subscribe({
        next: (data) => {
          this.readingsSubject.next(Array.isArray(data) ? data : []);
        },
        error: (err) => {
          console.error('PLC API error:', err);
        }
      });
  }

  stop(): void {
    this.stopCurrentStream();
  }

  private stopCurrentStream(): void {
    this.stop$.next();
    this.stop$.complete();
    this.stop$ = new Subject<void>();
  }
}