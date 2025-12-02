import { Component, computed, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { LikeService } from '../../../services/like.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-like-button',
  imports: [MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule],
  templateUrl: './like-button.component.html',
  styleUrl: './like-button.component.css'
})
export class LikeButtonComponent {
@Input({ required: true }) projectId!: number;

  // 🔥 1. הגדרת סטר (Setter) עבור הקלט:
  // זה מבטיח שכל ערך חדש שנכנס מבחוץ מעדכן את ה-Signal.
  @Input() set isLiked(value: boolean) {
    this.likeState.set(value);
  }

  @Output() toggled = new EventEmitter<boolean>();

  private likeService = inject(LikeService);
  private authService = inject(AuthService);

  loading = signal(false);

  // 🔥 2. הוספת Signal למצב הפנימי (במקום המשתנה הישן)
  likeState = signal(false);

  isLoggedIn = computed(() => this.authService.currentUser() !== null);

  toggle() {
    // 🔥 3. קוראים למצב מה-Signal
    const currentState = this.likeState();

    if (this.loading() || !this.isLoggedIn()) return;

    this.loading.set(true);
    const request = currentState // 🔥 משתמשים ב-currentState
      ? this.likeService.removeFromLiked(this.projectId)
      : this.likeService.addToLiked(this.projectId);

    request.subscribe({
      next: () => {
        const newState = !currentState;
        this.likeState.set(newState); // 🔥 4. מעדכנים את ה-Signal
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
