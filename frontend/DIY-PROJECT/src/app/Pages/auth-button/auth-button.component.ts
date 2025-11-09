import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { AsyncPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-auth-button',
  imports: [MatIcon,AsyncPipe, MatButtonModule, MatMenuModule, MatIconModule],
  templateUrl: './auth-button.component.html',
  styleUrl: './auth-button.component.css'
})
export class AuthButtonComponent {

constructor(public auth: AuthService, private router: Router) {}

  goToLogin() {
    this.router.navigate(['/sign-in']);
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }
}
