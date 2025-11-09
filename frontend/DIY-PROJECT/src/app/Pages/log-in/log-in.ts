// auth/components/login/login.component.ts

import { ReactiveFormsModule, Validators, FormBuilder, FormGroup } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { Component } from "@angular/core";

@Component({
  selector: 'app-log-in',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './log-in.component.html',  // ← תיקון: ללא מקף
  styleUrls: ['./log-in.component.css']     // ← תיקון: ללא מקף
})
export class LoginComponent {
  form!: FormGroup;  // ← נשתמש ב-! כי נאתחל ב-constructor
  error = '';
  loading = false;

  constructor(
    private fb: FormBuilder,
    public auth: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      identifier: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';

    this.auth.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/profile']),
      error: (err) => {
        this.error = err.error?.error || 'שם משתמש או סיסמה שגויים';
        this.loading = false;
      }
    });
  }

  googleLogin() {
    this.auth.loginWithGoogle();
  }
}