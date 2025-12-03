// src/app/shared/components/message-bubble/message-bubble.component.ts
import { Component, input } from '@angular/core'; // הוסר computed
import { Clipboard } from '@angular/cdk/clipboard';
import { MarkdownPipe } from '../../../../markdown.pipe';

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

@Component({
  selector: 'app-message-bubble',
  standalone: true,
  imports: [MarkdownPipe],
  templateUrl: './message-bubble.component.html',
  styleUrl: './message-bubble.component.css'
})
export class MessageBubbleComponent {
  message = input.required<ChatMessage>();
  showCopied = false;
  
  constructor(private clipboard: Clipboard) {}

  copy() {
    this.clipboard.copy(this.message().content);
    this.showCopied = true;
    setTimeout(() => this.showCopied = false, 2000);
  }
}