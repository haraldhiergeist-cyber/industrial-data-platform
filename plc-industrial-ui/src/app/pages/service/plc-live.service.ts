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
      console.log('STOMP already active');
      return;
    }

    console.log('STOMP connect() called');

    this.client = new Client({
      brokerURL: 'ws://localhost:8082/ws',
      reconnectDelay: 5000,
      debug: (msg) => console.log('[STOMP]', msg)
    });

    this.client.onConnect = () => {
      console.log('STOMP connected');

      this.client?.subscribe('/topic/plc-updates', (message: IMessage) => {
        console.log('STOMP raw message', message.body);
        const payload = JSON.parse(message.body) as PlcReadingEvent;
        this.readingEventsSubject.next(payload);
      });
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP broker error', frame);
    };

    this.client.onWebSocketClose = (event) => {
      console.warn('WebSocket closed', event);
    };

    this.client.onWebSocketError = (event) => {
      console.error('WebSocket error', event);
    };

    this.client.activate();
  }

  disconnect(): void {
    console.log('STOMP disconnect() called');
    void this.client?.deactivate();
    this.client = undefined;
  }
}