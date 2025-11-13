import { Component, computed, Input, signal } from '@angular/core';
import { Project } from '../models/project.model';
import { AvatarHelperService } from '../services/avatar-helper.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-project-header',
  imports: [CommonModule],
  templateUrl: './project-header.component.html',
  styleUrl: './project-header.component.css'
})
export class ProjectHeaderComponent {
// שם שונה! לא project – אלא projectInput
  @Input({ required: true }) set projectInput(value: Project) {
    this._project.set(value);
  }

  // signal פנימי
  private _project = signal<Project | null>(null);

  // חושף רק לקריאה
  project = this._project.asReadonly();

  private avatarHelper = new AvatarHelperService();

  // תמונת הפרויקט
  getImageUrl(): string {
    const base64 = this.project()?.picture;
    if (base64) return `data:image/jpeg;base64,${base64}`;
    return 'https://picsum.photos/800/400?random=' + (this.project()?.id || 1);
  }

  // אווטאר – computed
  avatar = computed(() => {
    const user = this.project()?.users;
    if (!user) {
      return { url: '', initial: '?', color: '#999' };
    }

    const path = user.profilePicturePath;
    const name = user.userName || 'אנונימי';

    if (path) {
      return { url: `http://localhost:8080${path}`, initial: '', color: '' };
    }

    return {
      url: '',
      initial: this.avatarHelper.getFirstInitial(name),
      color: this.avatarHelper.generateColor(name)
    };
  });
}