export type MetricType = 'TEMPERATURE' | 'PRESSURE' | 'LEVEL';

export type TimeRange =
  | 'LAST_5_MINUTES'
  | 'LAST_15_MINUTES'
  | 'LAST_1_HOUR'
  | 'LAST_24_HOURS';

export interface MeasurementHistoryItem {
  timestamp: string;
  value: number;
}

export interface MeasurementHistoryPointDto {
  timestamp?: string;
  value?: number | string;
}

export interface MetricOption {
  label: string;
  value: MetricType;
}

export interface TimeRangeOption {
  label: string;
  value: TimeRange;
}

export interface MetricConfig {
  label: string;
  unit: string;
  color: string;
  backgroundColor: string;
}

export const METRIC_CONFIG: Record<MetricType, MetricConfig> = {
  TEMPERATURE: {
    label: 'Temperature',
    unit: '°C',
    color: '#5aa9ff',
    backgroundColor: 'rgba(90, 169, 255, 0.15)'
  },
  PRESSURE: {
    label: 'Pressure',
    unit: 'bar',
    color: '#34d399',
    backgroundColor: 'rgba(52, 211, 153, 0.15)'
  },
  LEVEL: {
    label: 'Level',
    unit: '%',
    color: '#f59e0b',
    backgroundColor: 'rgba(245, 158, 11, 0.15)'
  }
};

export const METRIC_OPTIONS: MetricOption[] = [
  { label: 'Temperature', value: 'TEMPERATURE' },
  { label: 'Pressure', value: 'PRESSURE' },
  { label: 'Level', value: 'LEVEL' }
];

export const TIME_RANGE_OPTIONS: TimeRangeOption[] = [
  { label: 'Last 5 min', value: 'LAST_5_MINUTES' },
  { label: 'Last 15 min', value: 'LAST_15_MINUTES' },
  { label: 'Last 1 hour', value: 'LAST_1_HOUR' },
  { label: 'Last 24 hours', value: 'LAST_24_HOURS' }
];