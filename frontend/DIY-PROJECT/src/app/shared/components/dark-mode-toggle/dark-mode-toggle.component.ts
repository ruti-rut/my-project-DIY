import { DOCUMENT } from '@angular/common';
import { Component, Inject } from '@angular/core';

@Component({
  selector: 'app-dark-mode-toggle',
  imports: [],
  templateUrl: './dark-mode-toggle.component.html',
  styleUrl: './dark-mode-toggle.component.css'
})
export class DarkModeToggleComponent {
isDark = false;

  constructor(@Inject(DOCUMENT) private document: Document) {}

  ngOnInit() {
    const saved = localStorage.getItem('darkMode') === 'true';
    this.isDark = saved;
    this.updateTheme();
  }

  toggle() {
    this.isDark = !this.isDark;
    localStorage.setItem('darkMode', this.isDark.toString());
    this.updateTheme();
  }

  private updateTheme() {
    if (this.isDark) {
      this.document.body.classList.add('dark-theme');
    } else {
      this.document.body.classList.remove('dark-theme');
    }
  }
}
