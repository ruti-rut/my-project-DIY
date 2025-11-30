import { Component, computed, inject } from '@angular/core';
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
 avatar = computed(() => {
  const p = this.profile();

  // אם עדיין אין פרופיל בכלל
  if (!p || !p.userName) {
    return { url: '', initial: '?', color: '#888888' };
  }

  // 1. base64
  if (p.profilePicture) {
    return { 
      url: p.profilePicture.startsWith('data:') 
        ? p.profilePicture 
        : `data:image/jpeg;base64,${p.profilePicture}`, 
      initial: '', 
      color: '' 
    };
  }

  // 2. path
  if (p.profilePicturePath) {
    return { 
      url: `http://localhost:8080${p.profilePicturePath}`, 
      initial: '', 
      color: '' 
    };
  }

  // 3. אווטאר צבעוני – בטוח שיש שם
  const name = p.userName.trim();
  return {
    url: '',
    initial: this.avatarHelper.getFirstInitial(name),
    color: this.avatarHelper.generateColor(name)
  };
});


  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('he-IL', { year: 'numeric', month: 'long', day: 'numeric' });
  }
}
