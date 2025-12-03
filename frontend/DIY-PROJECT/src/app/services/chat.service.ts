// src/app/services/chat.service.ts

import { Injectable, signal, effect } from '@angular/core';

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private baseUrl = 'http://localhost:8080/api/AIAssistant';

  private conversationId = signal<string>(this.loadOrCreateConversationId());

  messages = signal<ChatMessage[]>([]);
  isLoading = signal(false);

  private currentAbortController: AbortController | null = null;

  // משתנים שמשמשים רק בתוך ה-stream
  private buffer = '';
  private reader!: ReadableStreamDefaultReader<Uint8Array>;
  private decoder = new TextDecoder('utf-8');

  constructor() {
    effect(() => {
      localStorage.setItem('diy-chat-conversation-id', this.conversationId());
    });
  }

  private loadOrCreateConversationId(): string {
    const saved = localStorage.getItem('diy-chat-conversation-id');
    return saved || crypto.randomUUID();
  }

  sendMessage(userText: string) {
    if (!userText.trim() || this.isLoading()) return;

    // הודעת משתמש
    this.messages.update(msgs => [
      ...msgs,
      { role: 'user', content: userText, timestamp: new Date() }
    ]);

    this.isLoading.set(true);

    // הודעה ריקה של העוזר – תתמלא בהדרגה
    this.messages.update(msgs => [
      ...msgs,
      { role: 'assistant', content: '', timestamp: new Date() }
    ]);

    this.currentAbortController = new AbortController();

    const url = `${this.baseUrl}/chat?message=${encodeURIComponent(userText)}&conversationId=${this.conversationId()}`;

    fetch(url, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Accept': 'text/event-stream' },
      signal: this.currentAbortController.signal
    })
      .then(response => {
        if (response.status === 401) {
          this.handleAuthError();
          return;
          return;
        }
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        this.buffer = '';
        this.reader = response.body!.getReader();

        this.processStream();
      })
      .catch(err => {
        if (err.name === 'AbortError') {
          console.log('Fetch aborted by user');
          return;
        }
        console.error('Fetch error:', err);
        this.handleStreamError(err);
      });
  }

  private async processStream() {
    try {
      while (true) {
        const { done, value } = await this.reader.read();

        if (done) {
          console.log('Stream complete');
          this.isLoading.set(false);
          return;
        }

        this.buffer += this.decoder.decode(value, { stream: true });

        const lines = this.buffer.split('\n');
        this.buffer = lines.pop() || ''; // שומרים שורה חלקית להמשך

        for (const line of lines) {
          // דילוג על שורות ריקות או לא רלוונטיות
          if (!line.startsWith('data:')) continue;

          const dataPart = line.slice(5); // חותך בדיוק "data:"

          // שורה ריקה = data: בלבד → ירידת שורה ב-markdown
          if (line === 'data:' || dataPart.trim() === '') {
            this.appendToLastMessage('\n');
            continue;
          }

          const data = dataPart.trim();

          if (data === '[DONE]' || data === ' [DONE]') {
            console.log('Received [DONE]');
            this.isLoading.set(false);
            return;
          }

          if (data) {
            this.appendToLastMessage(data);
          }
        }
      }
    } catch (err: any) {
      if (err.name === 'AbortError') {
        console.log('Stream aborted by user');
        return;
      }
      console.error('Stream reading error:', err);
      this.handleStreamError(err);
    }
  }

  // פונקציית עזר נקייה ומהירה
  private appendToLastMessage(text: string) {
    this.messages.update(msgs => {
      const last = msgs[msgs.length - 1];
      if (last?.role === 'assistant') {
        return [
          ...msgs.slice(0, -1),
          { ...last, content: last.content + text }
        ];
      }
      return msgs;
    });
  }

  private handleStreamError(err: Error) {
    this.isLoading.set(false);

    this.messages.update(msgs => {
      const last = msgs[msgs.length - 1];
      if (last?.role === 'assistant' && !last.content.trim()) {
        return [
          ...msgs.slice(0, -1),
          {
            role: 'assistant',
            content: `**שגיאה:** ${err.message || 'החיבור נכשל'}`,
            timestamp: new Date()
          }
        ];
      }
      return msgs;
    });
  }

  private handleAuthError() {
    this.isLoading.set(false);
    this.messages.update(msgs => [
      ...msgs.slice(0, -1),
      {
        role: 'assistant',
        content: '**נדרשת התחברות מחדש.** נא להתחבר שוב.',
        timestamp: new Date()
      }
    ]);
  }

  newConversation() {
    if (this.currentAbortController) {
      this.currentAbortController.abort();
      this.currentAbortController = null;
    }

    this.conversationId.set(crypto.randomUUID());
    this.messages.set([]);
    this.isLoading.set(false);
  }

  clearAll() {
    localStorage.removeItem('diy-chat-conversation-id');
    this.newConversation();
  }
}