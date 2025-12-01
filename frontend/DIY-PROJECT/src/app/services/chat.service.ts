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

  private baseUrl = 'http://localhost:8080/api/AIAssistant'; // תתקני אם הפורט שלך שונה

  // מזהה השיחה – נשמר גם ב-localStorage כדי שימשיך אחרי רענון דף
  private conversationId = signal<string>(this.loadOrCreateConversationId());

  // כל ההודעות
  messages = signal<ChatMessage[]>([]);

  // מצב טעינה
  isLoading = signal(false);

  // SSE stream פתוח כרגע (כדי שנוכל לסגור אותו אם צריך)
  private currentEventSource: EventSource | null = null;

  constructor() {
    // שמירה אוטומטית של conversationId ב-localStorage
    effect(() => {
      localStorage.setItem('diy-chat-conversation-id', this.conversationId());
    });

    // אם יש conversationId ישן – אפשר לטעון הודעות מהשרת בעתיד (לא חובה עכשיו)
  }

  private loadOrCreateConversationId(): string {
    const saved = localStorage.getItem('diy-chat-conversation-id');
    if (saved) return saved;
    return crypto.randomUUID();
  }

  sendMessage(userText: string) {
    if (!userText.trim() || this.isLoading()) return;

    // 1. הודעת המשתמש
    this.messages.update(msgs => [
      ...msgs,
      { role: 'user', content: userText, timestamp: new Date() }
    ]);

    this.isLoading.set(true);

    // הוספת הודעת assistant ריקה שתתמלא בזמן אמת
    this.messages.update(msgs => [
      ...msgs,
      { role: 'assistant', content: '', timestamp: new Date() }
    ]);

    const url = `${this.baseUrl}/chat?message=${encodeURIComponent(userText)}&conversationId=${this.conversationId()}`;

    fetch(url, {
      method: 'GET',
      credentials: 'include',                 // חובה בשביל ה‑cookie של Spring Security
      headers: { 'Accept': 'text/event-stream' }
    })
    .then(response => {
      if (response.status === 401) {
        this.handleAuthError();
        return;
      }
      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      const reader = response.body!.getReader();
      const decoder = new TextDecoder('utf-8');
      let buffer = '';

      const processChunk = () => {
        reader.read().then(({ done, value }) => {
          if (done) {
            this.isLoading.set(false);
            return;
          }

          buffer += decoder.decode(value, { stream: true });

          // פיצול לפי שורות SSE
          const lines = buffer.split('\n');
          buffer = lines.pop() ?? '';               // השורה האחרונה עלולה להיות חלקית

          for (const line of lines) {
            if (line.startsWith('data: ')) {
              const data = line.slice(6).trim();

              if (data === '[DONE]') {
                this.isLoading.set(false);
                return;
              }

              // עדכון זורם של ההודעה האחרונה
              this.messages.update(msgs => {
                const last = msgs[msgs.length - 1];
                if (last.role === 'assistant') {
                  return [
                    ...msgs.slice(0, -1),
                    { ...last, content: last.content + data }
                  ];
                }
                return msgs;
              });
            }
          }

          processChunk(); // ממשיך לקרוא את הצ'אנק הבא
        });
      };

      processChunk();
    })
    .catch(err => {
      console.error(err);
      this.isLoading.set(false);
      this.messages.update(msgs => [
        ...msgs.slice(0, -1), // מסיר את ההודעה הריקה שהוספנו
        {
          role: 'assistant',
          content: `שגיאה: ${err.message || 'חיבור נכשל'}`,
          timestamp: new Date()
        }
      ]);
    });
  }

  private handleAuthError() {
    this.isLoading.set(false);
    this.messages.update(msgs => [
      ...msgs,
      {
        role: 'assistant',
        content: 'נדרשת התחברות מחדש.',
        timestamp: new Date()
      }
    ]);
  }
  // שיחה חדשה לגמרי
  newConversation() {
    this.conversationId.set(crypto.randomUUID());
    this.messages.set([]);
    if (this.currentEventSource) {
      this.currentEventSource.close();
      this.currentEventSource = null;
    }
    this.isLoading.set(false);
  }

  // אם תרצי בעתיד לנקות הכל
  clearAll() {
    localStorage.removeItem('diy-chat-conversation-id');
    this.newConversation();
  }
}