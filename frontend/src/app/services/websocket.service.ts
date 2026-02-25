import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import * as SockJS from 'sockjs-client';
import { Client, Message, over } from 'stompjs';

export interface WebSocketMessage {
  type: string;
  collaborationId: number;
  payload: any;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client | null = null;
  private messageSubject = new Subject<WebSocketMessage>();
  private connected = false;

  constructor() {}

  connect(collaborationId: number): Observable<WebSocketMessage> {
    if (this.connected) {
      return this.messageSubject.asObservable();
    }

    const socket = new SockJS('http://localhost:8082/ws');
    this.stompClient = over(socket);

    // Disable debug logging
    this.stompClient.debug = () => {};

    this.stompClient.connect({}, () => {
      console.log('WebSocket connected for collaboration:', collaborationId);
      this.connected = true;

      // Subscribe to collaboration-specific topic
      this.stompClient!.subscribe(`/topic/collaboration/${collaborationId}`, (message: Message) => {
        const wsMessage: WebSocketMessage = JSON.parse(message.body);
        console.log('WebSocket message received:', wsMessage);
        this.messageSubject.next(wsMessage);
      });
    }, (error: any) => {
      console.error('WebSocket connection error:', error);
      this.connected = false;
      
      // Attempt to reconnect after 5 seconds
      setTimeout(() => {
        console.log('Attempting to reconnect WebSocket...');
        this.connect(collaborationId);
      }, 5000);
    });

    return this.messageSubject.asObservable();
  }

  disconnect(): void {
    if (this.stompClient && this.connected) {
      this.stompClient.disconnect(() => {
        console.log('WebSocket disconnected');
        this.connected = false;
      });
    }
  }

  isConnected(): boolean {
    return this.connected;
  }
}
