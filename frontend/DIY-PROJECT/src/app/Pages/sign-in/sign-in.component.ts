import { Component, inject, signal } from '@angular/core';
import { 
  Validators, 
  FormBuilder, 
  ReactiveFormsModule, 
  FormGroup, 
  FormControl,
  AbstractControl,
  ValidatorFn 
} from '@angular/forms';
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


function conditionalEmailValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const value = (control.value ?? '').trim();

    // אם אין @ → זה שם משתמש, אין צורך בוולידציית אימייל
    if (!value.includes('@')) {
      return null;
    }

    // אם יש @ → בדוק שזה אימייל תקין
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(value) ? null : { email: true };
  };
}


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

  // השתמש בוולידטור המותאם במקום Validators.email
  loginForm = new FormGroup({
    identifier: new FormControl('', [Validators.required, conditionalEmailValidator()]),
    password: new FormControl('', [Validators.required])
  });

  onLoginSubmit(): void {
    if (this.loginForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.authService.signIn(this.loginForm.value as UserLogInDTO).subscribe({
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