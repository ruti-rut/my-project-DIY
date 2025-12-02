import { Component, inject, Input } from '@angular/core';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { UserResponseDTO } from '../../../../models/user.model';
import { AuthService } from '../../../../services/auth.service';
import { AvatarHelperService } from '../../../../services/avatar-helper.service';

@Component({
  selector: 'app-user-profile-menu',
  imports: [CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule],
  templateUrl: './user-profile-menu.component.html',
  styleUrl: './user-profile-menu.component.css'
})
export class UserProfileMenuComponent {
@Input({ required: true }) user!: UserResponseDTO;

  // הזרקת שירותים
  private authService = inject(AuthService);
  private avatarHelper = inject(AvatarHelperService);

  // חשיפת פונקציות העזר ל-HTML
  public getFirstInitial = this.avatarHelper.getFirstInitial.bind(this.avatarHelper);
  public generateColor = this.avatarHelper.generateColor.bind(this.avatarHelper);
  
  /**
   * מפעיל את תהליך ההתנתקות
   */
  logout(): void {
    this.authService.logout().subscribe();
  }
}
