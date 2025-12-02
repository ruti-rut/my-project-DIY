import { Component, computed, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../../services/auth.service';
import { FavoriteService } from '../../../../services/favorite.service';

@Component({
  selector: 'app-favorite-button',
  imports: [MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule],
  templateUrl: './favorite-button.component.html',
  styleUrl: './favorite-button.component.css'
})
export class FavoriteButtonComponent {
  @Input({ required: true }) projectId!: number;

  // 🔥 1. הגדרת סטר (Setter) עבור הקלט:
  // זה מבטיח שכל ערך חדש שנכנס מבחוץ מעדכן את ה-Signal.
  @Input() set isFavorited(value: boolean) {
    this.favoritedState.set(value);
  }

  @Output() toggled = new EventEmitter<boolean>();

  private favoriteService = inject(FavoriteService);
  private authService = inject(AuthService);

  loading = signal(false);

  // 🔥 2. הוספת Signal למצב הפנימי (במקום המשתנה הישן)
  favoritedState = signal(false);

  isLoggedIn = computed(() => this.authService.currentUser() !== null);

  toggle() {
    // 🔥 3. קוראים למצב מה-Signal
    const currentState = this.favoritedState();

    if (this.loading() || !this.isLoggedIn()) return;

    this.loading.set(true);
    const request = currentState // 🔥 משתמשים ב-currentState
      ? this.favoriteService.removeFromFavorites(this.projectId)
      : this.favoriteService.addToFavorites(this.projectId);

    request.subscribe({
      next: () => {
        const newState = !currentState;
        this.favoritedState.set(newState); // 🔥 4. מעדכנים את ה-Signal
        this.toggled.emit(newState);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        alert('שגיאה');
      }
    });
  }
}