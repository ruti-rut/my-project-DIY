import { Component, inject, signal } from '@angular/core';
import { ProjectService } from '../../../services/project.service';
import { ProfileService } from '../../../services/profile.service';
import { MatIcon } from "@angular/material/icon";
import { ProjectCardComponent } from "../project-card/project-card/project-card.component";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-drafts-tab',
  imports: [MatIcon, ProjectCardComponent, MatProgressSpinner,RouterLink],
  templateUrl: './drafts-tab.component.html',
  styleUrl: './drafts-tab.component.css'
})
export class DraftsTabComponent {
  private profileService = inject(ProfileService);
  private projectService = inject(ProjectService);
  draftProjects = signal<any[]>([]);
  loading = signal(true);

  ngOnInit(): void {
    this.loadDrafts();
  }

  loadDrafts(): void {
    this.loading.set(true);
    this.projectService.getMyDrafts().subscribe({
      next: (projects) => {
        this.draftProjects.set(projects);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load drafts', err);
        this.loading.set(false);
      }
    });
  }
  deleteProject(projectId: number) {
    if (!confirm('למחוק את הפרויקט לצמיתות? לא ניתן לשחזר!')) return;

    // 🔥 שימוש ב-ProjectService
    this.projectService.deleteProject(projectId).subscribe({
      next: () => {
        // עדכון לוקאלי של הרשימה (מחיקה מה-signal)
        this.draftProjects.update(projects => projects.filter(x => x.id !== projectId));
        // עדכון מונה ב-Header
        this.profileService.deleteMyProject(projectId);
      },
      error: () => alert('שגיאה במחיקה')
    });
  }


}
