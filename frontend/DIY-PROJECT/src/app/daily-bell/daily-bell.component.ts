import { Component, computed, inject } from '@angular/core';
import { UserService } from '../services/user.service';
import { NgClass } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-daily-bell',
  imports: [MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    NgClass],
  templateUrl: './daily-bell.component.html',
  styleUrl: './daily-bell.component.css'
})
export class DailyBellComponent {
  private userService = inject(UserService);
  private authService = inject(AuthService);

  // computed שמסתכל על ה-signal של AuthService
  isSubscribed = computed(() => {
    const value = this.authService.currentUser()?.isSubscribedToDaily ?? false;
    console.log('DailyBell: computed isSubscribed =', value);
    return value;
  });

  justToggled = false;

  toggleSubscription() {
    console.log('DailyBell: toggleSubscription clicked');
    this.justToggled = true;

    this.userService.toggleDailySubscription().subscribe({
      next: (user) => {
        console.log('DailyBell: toggleSubscription response', user);
        setTimeout(() => this.justToggled = false, 800);
      },
      error: (err) => {
        console.error('DailyBell: toggleSubscription error', err);
        this.justToggled = false;
      }
    });
  }
}
