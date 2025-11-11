// src/app/components/header/header.component.ts
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { LoginButtonComponent } from '../../login-button/login-button.component';
import { UserProfileMenuComponent } from '../../user-profile-menu/user-profile-menu.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
CommonModule,
    RouterLink,
    RouterLinkActive,
    
    // Material
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    
    // הילדים שהפרדנו
    LoginButtonComponent,
    UserProfileMenuComponent],
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})
export class HeaderComponent {
  private authService = inject(AuthService);
  
  // חשיפה ישירה של ה-Signal ל-Template
  currentUser = this.authService.currentUser;
}