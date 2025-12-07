import { DOCUMENT } from '@angular/common';
import { Component, Inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-dark-mode-toggle',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatTooltipModule], // Import Material modules
  templateUrl: './dark-mode-toggle.component.html',
  styleUrl: './dark-mode-toggle.component.css'
})
export class DarkModeToggleComponent implements OnInit {
  isDark = signal(false); // Using Signal for better performance

  constructor(@Inject(DOCUMENT) private document: Document) {}

  ngOnInit() {
    // 1. Check LocalStorage
    const saved = localStorage.getItem('darkMode');
    
    if (saved !== null) {
      this.isDark.set(saved === 'true');
    } else {
      // 2. If no saved preference, check System Settings
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.isDark.set(prefersDark);
    }

    this.updateTheme();
  }

  toggle() {
    this.isDark.update(val => !val); // Flip value
    localStorage.setItem('darkMode', this.isDark().toString());
    this.updateTheme();
  }

  private updateTheme() {
    if (this.isDark()) {
      this.document.body.classList.add('dark-theme');
    } else {
      this.document.body.classList.remove('dark-theme');
    }
  }
}