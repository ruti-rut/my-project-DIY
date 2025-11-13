import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Project } from '../models/project.model';
import { ProjectService } from '../services/project.service';
import { ProjectHeaderComponent } from '../project-header/project-header.component';
import { AsyncPipe } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProjectCommentsComponent } from '../project-comments/project-comments.component';
import { ProjectStepsComponent } from '../project-steps/project-steps.component';

@Component({
  selector: 'app-project-detail',
  imports: [
    MatProgressSpinnerModule,
    ProjectHeaderComponent,
    ProjectStepsComponent,
    ProjectCommentsComponent],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.css'
})
export class ProjectDetailComponent {
  private route = inject(ActivatedRoute);
  private projectService = inject(ProjectService);

  projectId = this.route.snapshot.paramMap.get('id')!;
  project = signal<Project | null>(null);
  loading = signal(true);

  constructor() {
    this.loadProject();
  }

  loadProject() {
    this.projectService.getById(+this.projectId).subscribe({
      next: (proj) => {
        this.project.set(proj);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        alert('שגיאה בטעינת הפרויקט');
      }
    });
  }

}
