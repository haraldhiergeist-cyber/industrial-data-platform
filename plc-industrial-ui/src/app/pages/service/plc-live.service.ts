import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import { PlcReadingEvent } from '../../api';

@Injectable({
  providedIn: 'root'
})
export class PlcLiveService {
  private client?: Client;
  private readonly readingEventsSubject = new Subject<PlcReadingEvent>();

  readonly readingEvents$ = this.readingEventsSubject.asObservable();

  connect(): void {
    if (this.client?.active) {
      return;
    }

    this.client = new Client({
      brokerURL: 'ws://localhost:8082/ws',
      reconnectDelay: 5000,
      debug: () => {}
    });

    this.client.onConnect = () => {
      this.client?.subscribe('/topic/plc-updates', (message: IMessage) => {
        const payload = JSON.parse(message.body) as PlcReadingEvent;
        this.readingEventsSubject.next(payload);
      });
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP broker error', frame);
    };

    this.client.onWebSocketClose = () => {
      console.warn('WebSocket closed');
    };

    this.client.activate();
  }

  disconnect(): void {
    void this.client?.deactivate();
    this.client = undefined;
  }
}