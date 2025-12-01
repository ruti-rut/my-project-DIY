// src/app/components/challenge-details/submit-project-dialog/submit-project-dialog.component.ts

import { Component, inject, signal, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { ProjectService } from '../services/project.service';
import { ProjectListDTO } from '../models/project.model';

export interface SubmitProjectDialogData {
  challengeId: number;
}

@Component({
  selector: 'app-submit-project-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './submit-project-dialog.component.html',
  styleUrl: './submit-project-dialog.component.css'
})
export class SubmitProjectDialogComponent implements OnInit {
  readonly dialogRef = inject(MatDialogRef<SubmitProjectDialogComponent>);
  readonly data = inject<SubmitProjectDialogData>(MAT_DIALOG_DATA);

  private router = inject(Router);
  private projectService = inject(ProjectService);

  myProjects = signal<ProjectListDTO[]>([]);
  loading = signal(true);

  ngOnInit(): void {
    this.projectService.getMyProjects().subscribe({
      next: (projects) => {
        this.myProjects.set(projects.filter(p => !p.challengeId));
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  createNewProject(): void {
    this.dialogRef.close();
    this.router.navigate(['/create-project'], {
      queryParams: { challengeId: this.data.challengeId }
    });
  }
  submitExistingProject(projectId: number): void {
    this.projectService.assignProjectToChallenge(projectId, this.data.challengeId).subscribe({
      next: () => {
        this.dialogRef.close();
        // רענון עדין יותר – בלי reload של כל הדף
        this.router.navigate(['/challenge', this.data.challengeId]).then(() => {
          window.location.reload(); // או שתעדכני את הרשימה ב-signal אם תרצי
        });
      },
      error: () => {
        alert('לא ניתן להגיש – ייתכן שהאתגר נסגר או שיש בעיה');
      }
    });
  }

  // ← הפונקציה שהייתה חסרה!
  getImageUrl(project: ProjectListDTO): string {
    if (project.picture) {
      return `data:image/jpeg;base64,${project.picture}`;
    }
    return '/assets/default-project.jpg';
  }
  navigateToMyProjects(): void {
  this.dialogRef.close(); // סגירת הדיאלוג
  // ניווט לדף הפרויקטים האישיים והעברת challengeId כפרמטר ב-URL
  this.router.navigate(['/my-projects'], {
    queryParams: { challengeId: this.data.challengeId } 
  });
}
}