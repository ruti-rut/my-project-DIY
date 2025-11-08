// auth/login/login.component.ts
import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LoginComponent {
  loginForm = this.fb.group({
    identifier: ['', Validators.required], // שם משתמש או מייל
    password: ['', Validators.required]
  });
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: () => this.router.navigate(['/profile']),
        error: (err) => {
          this.errorMessage = err.error?.error || 'שם משתמש או סיסמה שגויים.';
          console.error('שגיאת כניסה:', err);
        }
      });
    }
  }
  
  /** מפנה ל-Backend לטיפול בכניסה עם גוגל (OAuth2) */
  loginWithGoogle(): void {
    // ה-Backend יטפל בהפניה לגוגל ויחזיר עוגייה
    window.location.href = 'http://localhost:8080/oauth2/authorization/google'; 
  }
}