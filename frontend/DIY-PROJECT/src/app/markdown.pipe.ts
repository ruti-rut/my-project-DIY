// src/app/pipes/markdown.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import { marked } from 'marked';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'markdown',
  standalone: true
})
export class MarkdownPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {
    // Configure marked for better rendering
    marked.setOptions({
      gfm: true,
      breaks: true, // ✅ חובה לירידות שורה

    });
  }

  transform(value: string): SafeHtml {
    if (!value) return '';
    
    try {
      // Convert markdown to HTML
      const html = marked.parse(value) as string;
      
      // Sanitize and return
      return this.sanitizer.bypassSecurityTrustHtml(html);
    } catch (error) {
      console.error('Markdown parsing error:', error);
      // Fallback: return text with line breaks
      const escaped = value
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>');
      return this.sanitizer.bypassSecurityTrustHtml(escaped);
    }
  }
}