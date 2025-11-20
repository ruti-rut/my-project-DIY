import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ChatService } from '../services/chat.service';
import { NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageBubbleComponent } from '../message-bubble/message-bubble.component';

@Component({
  selector: 'app-diy-chat',
  imports: [FormsModule, MessageBubbleComponent],
  templateUrl: './diy-chat.component.html',
  styleUrl: './diy-chat.component.css'
})
export class DiyChatComponent {
  chatService = inject(ChatService);
  authService = inject(AuthService);
  router = inject(Router);

  messages = this.chatService.messages;
  isLoading = this.chatService.isLoading;
  newMessage = '';

  send() {
    const text = this.newMessage.trim();
    if (!text || this.isLoading()) return;

    this.chatService.sendMessage(text);
    this.newMessage = '';
  }

  startNewChat() {
    if (confirm('להתחיל שיחה חדשה?')) {
      this.chatService.newConversation();
    }
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/sign-in']);
    });
  }
// בתוך DiyChatComponent
handleEnter(event: Event) {
  const keyboardEvent = event as KeyboardEvent;
  if (keyboardEvent.shiftKey) {
    return; // Shift + Enter = ירידת שורה רגילה
  }
  
  keyboardEvent.preventDefault(); // מונע ירידת שורה
  this.send();
}
}
