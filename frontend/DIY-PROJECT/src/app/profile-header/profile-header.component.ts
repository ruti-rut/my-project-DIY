import { Component, inject } from '@angular/core';
import { AvatarHelperService } from '../services/avatar-helper.service';
import { ProfileService } from '../services/profile.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-profile-header',
  imports: [CommonModule, RouterModule, MatButtonModule],
  templateUrl: './profile-header.component.html',
  styleUrl: './profile-header.component.css'
})
export class ProfileHeaderComponent {
service = inject(ProfileService);
  avatarHelper = inject(AvatarHelperService);

  profile = this.service.profile;

  getInitial(): string {
    return this.avatarHelper.getFirstInitial(this.profile()?.userName);
  }

  getColor(): string {
    return this.avatarHelper.generateColor(this.profile()?.userName);
  }

  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('he-IL', { year: 'numeric', month: 'long', day: 'numeric' });
  }
}
