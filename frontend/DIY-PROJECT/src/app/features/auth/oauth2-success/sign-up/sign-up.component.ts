import { Component, inject, signal } from '@angular/core';
import { Validators, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UsersRegisterDTO } from '../../../../models/user.model';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sign-up',
  imports: [ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  MatProgressBarModule,RouterLink
],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})
export class SignUpComponent {
private authService = inject(AuthService);
  private router = inject(Router);

  errorMessage = signal<string | null>(null);
  isLoading = signal(false);

  signupForm = new FormGroup({
    userName: new FormControl('', [Validators.required, Validators.minLength(3)]),
    mail: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)])
  });

onSignupSubmit(): void {
  if (this.signupForm.invalid) {
    return;
  }

  this.isLoading.set(true);
  this.errorMessage.set(null);

  // הפתרון המושלם – בטוח 100% מפני undefined
  const signupData: UsersRegisterDTO = {
    userName: this.signupForm.get('userName')!.value!.trim(),
    mail: this.signupForm.get('mail')!.value!.trim(),
    password: this.signupForm.get('password')!.value!
  };

  this.authService.signUp(signupData).subscribe({
    next: (response) => {
      this.isLoading.set(false);
      console.log('הרשמה הצליחה!', response);
      this.router.navigate(['/login'], { queryParams: { registered: true } });
    },
    error: (err) => {
      this.isLoading.set(false);
      console.error('שגיאה:', err);
      this.errorMessage.set(
        err.status === 400
          ? 'שם משתמש או אימייל כבר קיימים.'
          : 'שגיאת שרת, נסה שוב מאוחר יותר.'
      );
    }
  });
}
}