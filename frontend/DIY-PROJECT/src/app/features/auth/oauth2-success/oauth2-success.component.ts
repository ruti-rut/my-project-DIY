import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-oauth2-success',
  imports: [MatProgressSpinnerModule],
  template: `
    <div style="text-align:center; padding:50px;">
      <h2>התחברת בהצלחה!</h2>
      <p>מעביר אותך לדף הבית...</p>
      <mat-spinner></mat-spinner>
    </div>
  `
})
export class OAuth2SuccessComponent implements OnInit {
  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.authService.checkAuthentication().subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.router.navigate(['/sing-in'])
    });
  }
}