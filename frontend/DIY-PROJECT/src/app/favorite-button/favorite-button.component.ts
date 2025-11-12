import { Component, computed, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FavoriteService } from '../services/favorite.service';
import { AuthService } from '../services/auth.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

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
  @Input() isFavorited = false;

  @Output() toggled = new EventEmitter<boolean>();  // חובה!

  private favoriteService = inject(FavoriteService);
  private authService = inject(AuthService);

  loading = signal(false);

  isLoggedIn = computed(() => this.authService.currentUser() !== null);

  toggle() {
    if (this.loading() || !this.isLoggedIn()) return;

    this.loading.set(true);
    const request = this.isFavorited
      ? this.favoriteService.removeFromFavorites(this.projectId)
      : this.favoriteService.addToFavorites(this.projectId);

    request.subscribe({
      next: () => {
        this.isFavorited = !this.isFavorited;
        this.toggled.emit(this.isFavorited);  // שולח boolean!
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        alert('שגיאה');
      }
    });
  }
}