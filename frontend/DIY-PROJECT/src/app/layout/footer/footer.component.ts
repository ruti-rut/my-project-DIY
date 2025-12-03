import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatChip } from '@angular/material/chips';
import { DailyBellComponent } from '../../shared/components/daily-bell/daily-bell/daily-bell.component';
import { LoginButtonComponent } from '../../shared/components/login-button/login-button/login-button.component';
import { UserProfileMenuComponent } from '../../shared/components/user-profile-menu/user-profile-menu/user-profile-menu.component';
import { DarkModeToggleComponent } from "../../shared/components/dark-mode-toggle/dark-mode-toggle.component";

@Component({
  selector: 'app-footer',
  imports: [
     CommonModule,
    RouterLink,
    RouterLinkActive,
    // Material
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatChip,
    // הילדים שהפרדנו
    LoginButtonComponent,
    UserProfileMenuComponent,
    DailyBellComponent,
    DarkModeToggleComponent
  ],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {

  private authService = inject(AuthService);
  
  // חשיפה ישירה של ה-Signal ל-Template
  currentUser = this.authService.currentUser;
}

