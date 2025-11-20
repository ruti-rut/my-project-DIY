// markdown.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import { marked } from 'marked';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({ name: 'markdown', standalone: true })
export class MarkdownPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string): SafeHtml {
    if (!value) return '';
    const html = marked(value, { gfm: true, breaks: true }); // ← סינכרוני לחלוטין
    return this.sanitizer.bypassSecurityTrustHtml(html as string);
  }
}