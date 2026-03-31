import { Injectable, computed, signal } from '@angular/core';
import { PlcReadingEvent } from '../../api';
import { PlcPollingService } from './plc-polling.service';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlcDashboardFacade {
  private pollingSub?: Subscription;

  readonly readings = signal<PlcReadingEvent[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly temperature = computed(() => this.findReading('temperature'));
  readonly pressure = computed(() => this.findReading('pressure'));
  readonly level = computed(() => this.findReading('level'));

  constructor(private pollingService: PlcPollingService) {}

  start(intervalMs = 1000): void {
    this.stop();

    this.loading.set(true);
    this.error.set(null);

    this.pollingSub = this.pollingService.watchAll(intervalMs).subscribe({
      next: (data) => {
        this.readings.set(Array.isArray(data) ? data : []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('PLC API error:', err);
        this.error.set('Failed to load PLC readings');
        this.loading.set(false);
      }
    });
  }

  stop(): void {
    this.pollingSub?.unsubscribe();
    this.pollingSub = undefined;
  }

  getReadingSignal(tagName: string) {
    return computed(() =>
      this.readings().find((r) => r.tagName === tagName)
    );
  }

  getValueSignal(tagName: string) {
    return computed(() =>
      this.readings().find((r) => r.tagName === tagName)?.valueAsString ?? '-'
    );
  }

  getQualitySignal(tagName: string) {
    return computed(() =>
      this.readings().find((r) => r.tagName === tagName)?.quality ?? 'BAD'
    );
  }

  private findReading(tagName: string): PlcReadingEvent | undefined {
    return this.readings().find((r) => r.tagName === tagName);
  }
}