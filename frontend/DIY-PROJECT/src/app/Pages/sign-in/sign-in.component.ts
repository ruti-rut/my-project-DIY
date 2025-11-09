import { Component, inject, signal } from '@angular/core';
import { Validators, FormBuilder, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { UserLogInDTO } from '../../models/user.model';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { CommonModule } from '@angular/common';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-sign-in',
  imports: [CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDividerModule],
  templateUrl: './sign-in.component.html',
  styleUrl: './sign-in.component.css'
})
export class SignInComponent {

  public authService = inject(AuthService);

  errorMessage = signal<string | null>(null);
  isLoading = signal(false);

  loginForm = new FormGroup({
    identifier: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  onLoginSubmit(): void {
    if (this.loginForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.authService.signIn(this.loginForm.value as UserLogInDTO).subscribe({
      // במקרה של הצלחה, ה-AuthService מנווט אוטומטית
      next: () => this.isLoading.set(false),
      error: (err) => {
        this.isLoading.set(false);
        if (err.status === 401) {
          this.errorMessage.set('אימייל או סיסמה שגויים. אנא נסה שנית.');
        } else {
          this.errorMessage.set('שגיאת שרת: נסה שוב מאוחר יותר.');
        }
      }
    });
  }
}