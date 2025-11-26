import { Component, computed, inject, Input, signal } from '@angular/core';
import { Project } from '../models/project.model';
import { AvatarHelperService } from '../services/avatar-helper.service';
import { CommonModule } from '@angular/common';
import { MatIcon } from "@angular/material/icon";
import { ProjectService } from '../services/project.service';
import { MatProgressSpinner } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-project-header',
  imports: [CommonModule, MatIcon, MatProgressSpinner],
  templateUrl: './project-header.component.html',
  styleUrl: './project-header.component.css'
})
export class ProjectHeaderComponent {
// שם שונה! לא project – אלא projectInput
  @Input({ required: true }) set projectInput(value: Project) {
    this._project.set(value);
  }
  private projectService = inject(ProjectService);  // אם אתה משתמש ב-inject

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


  downloadPdf(projectId: number) {
    this.projectService.downloadPdf(projectId).subscribe({
      next: (blob) => {
        // שם הקובץ לפי שם הפרויקט – יותר יפה!
        const projectTitle = this.project()?.title || 'project';
        const safeFileName = projectTitle.replace(/[^א-תa-zA-Z0-9]/g, '_') + '.pdf';

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = safeFileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('שגיאה בהורדת PDF', err);
        alert('לא ניתן להוריד את הקובץ כרגע');
      }
    });
  }

  print() {
    window.print();
  }

  // === שיתוף כללי (Web Share API + fallback) ===
  share() {
    const url = window.location.href;
    const title = this.project()?.title || 'פרויקט מדהים';

    if (navigator.share) {
      navigator.share({
        title: title,
        text: 'בואו תראו את הפרויקט היצירתי הזה! 🎨',
        url: url
      }).catch(() => {
        // אם המשתמש ביטל – אין בעיה
      });
    } else {
      // fallback למחשב
      navigator.clipboard.writeText(url);
      alert('הקישור הועתק ללוח! עכשיו תוכל/י לשתף אותו בווטסאפ, פייסבוק, אינסטגרם...');
    }
  }

  // === שיתוף ישיר לווטסאפ ===
  shareWhatsApp() {
    const url = window.location.href;
    const title = this.project()?.title || 'פרויקט מדהים';
    const text = `תראו איזה פרויקט יצירתי עשיתי! 😍✂️\n${title}\n`;
    const whatsappUrl = `https://api.whatsapp.com/send?text=${encodeURIComponent(text + url)}`;
    window.open(whatsappUrl, '_blank');
  }
}