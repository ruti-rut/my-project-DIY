import { HttpClient } from '@angular/common/http';
import { Component, computed, inject } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { ProjectCardComponent } from '../project-card/project-card.component';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-my-projects-tab',
  imports: [CommonModule,
    ProjectCardComponent,
    MatIconModule,
    MatButtonModule,
    RouterModule,
  MatIconModule],
  templateUrl: './my-projects-tab.component.html',
  styleUrl: './my-projects-tab.component.css'
})
export class MyProjectsTabComponent {
private profileService = inject(ProfileService);
  private http = inject(HttpClient);

myProjects = computed(() => this.profileService.profile()?.myProjects ?? []);

  deleteProject(projectId: number) {
    if (!confirm('למחוק את הפרויקט לצמיתות? לא ניתן לשחזר!')) return;

    this.http.delete(`/api/project/deleteProject/${projectId}`).subscribe({
      next: () => {
        this.profileService.deleteMyProject(projectId);
      },
      error: () => alert('שגיאה במחיקה')
    });
  }
}
