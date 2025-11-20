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

  private baseUrl = 'http://localhost:8080/api/users'; // תתקני אם הפורט שלך שונה

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

    // 1. מוסיפים מיד את הודעת המשתמש
    this.messages.update(msgs => [...msgs, {
      role: 'user',
      content: userText,
      timestamp: new Date()
    }]);

    this.isLoading.set(true);

    // סוגרים חיבור קיים אם יש (בטיחות)
    if (this.currentEventSource) {
      this.currentEventSource.close();
    }

    // 2. פותחים SSE חדש
    const url = `${this.baseUrl}/chat?message=${encodeURIComponent(userText)}&conversationId=${this.conversationId()}`;
    this.currentEventSource = new EventSource(url);

    let aiResponse = '';

    this.currentEventSource.onmessage = (event) => {
      const data = event.data;

      // Spring AI שולח [DONE] בסוף הסטרימינג
      if (data === '[DONE]' || data === 'data: [DONE]') {
        this.currentEventSource!.close();
        this.currentEventSource = null;
        this.isLoading.set(false);
        return;
      }

      // לפעמים מגיע עם "data: " לפני – ננקה
      const cleanChunk = data.replace(/^data: /, '');
      aiResponse += cleanChunk;

      // עדכון זורם של ההודעה האחרונה (או יצירת חדשה)
      this.messages.update(msgs => {
        const last = msgs[msgs.length - 1];
        if (last?.role === 'assistant') {
          return [...msgs.slice(0, -1), { ...last, content: aiResponse }];
        } else {
          return [...msgs, {
            role: 'assistant',
            content: aiResponse,
            timestamp: new Date()
          }];
        }
      });
    };

    this.currentEventSource.onerror = () => {
      this.currentEventSource!.close();
      this.currentEventSource = null;
      this.isLoading.set(false);

      if (!aiResponse) {
        this.messages.update(msgs => [...msgs, {
          role: 'assistant',
          content: 'אופס... יש בעיה בחיבור לשרת. נסה שוב מאוחר יותר.',
          timestamp: new Date()
        }]);
      }
    };
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