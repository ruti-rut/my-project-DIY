import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';
import { ChatService } from '../../../../services/chat.service';
import { MessageBubbleComponent } from '../../../../shared/components/message-bubble/message-bubble/message-bubble.component';

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
    if (confirm('Begin a new chat?')) {
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
