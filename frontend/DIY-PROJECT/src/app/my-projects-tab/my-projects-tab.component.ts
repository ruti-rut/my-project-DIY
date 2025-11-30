import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { ProjectCardComponent } from '../project-card/project-card.component';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { ProjectService } from '../services/project.service';
import { MatProgressSpinner } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-my-projects-tab',
  imports: [CommonModule,
    ProjectCardComponent,
    MatIconModule,
    MatButtonModule,
    RouterModule,
    MatIconModule, MatProgressSpinner],
  templateUrl: './my-projects-tab.component.html',
  styleUrl: './my-projects-tab.component.css'
})
export class MyProjectsTabComponent {
  private profileService = inject(ProfileService);
  private http = inject(HttpClient);
  private projectService = inject(ProjectService);
  myProjects = signal<any[]>([]);
  loading = signal(true); // לניהול מצב טעינה (ספינר)

  ngOnInit(): void {
    this.loadMyProjects();
  }

  loadMyProjects(): void {
    this.loading.set(true);
    this.projectService.getMyProjects().subscribe({
      next: (projects) => {
        this.myProjects.set(projects);
        this.loading.set(false);

        // 🔥 הערה חשובה: פונקציה זו מחזירה רק רשימה, לא מעדכנת את ה-ProfileService
        // אם תרצי שהמונה ב-Header יתעדכן, ה-ProfileService צריך לעשות Get לפרופיל מחדש.
      },
      error: (err) => {
        console.error('Failed to load my projects', err);
        this.loading.set(false);
      }
    });
  }

  // הפונקציה deleteProject נשארת כמעט זהה, רק נשתמש ב-signal המקומי
  deleteProject(projectId: number) {
    if (!confirm('למחוק את הפרויקט לצמיתות? לא ניתן לשחזר!')) return;

    this.http.delete(`/api/project/deleteProject/${projectId}`).subscribe({
      next: () => {
        // 1. עדכון לוקאלי של הרשימה (מחיקה מה-signal)
        this.myProjects.update(projects => projects.filter(x => x.id !== projectId));
        // 2. עדכון המונה הכללי ב-ProfileHeader (אם נדרש)
        this.profileService.deleteMyProject(projectId);
        // 🔥 זהו עדכון לוקאלי של המונה.
      },
      error: () => alert('שגיאה במחיקה')
    });
  }

  // נעדכן את ה-computed כדי שישתמש ב-signal המקומי
  myProjectsComputed = computed(() => this.myProjects());
}

