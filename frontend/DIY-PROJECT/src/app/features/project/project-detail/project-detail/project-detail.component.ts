import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Project } from '../../../../models/project.model';
import { ProjectService } from '../../../../services/project.service';
import { ProjectCommentsComponent } from '../../project-comments/project-comments/project-comments.component';
import { ProjectHeaderComponent } from '../../project-header/project-header/project-header.component';
import { ProjectStepsComponent } from '../../project-steps/project-steps/project-steps.component';
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: 'app-project-detail',
  imports: [
    MatProgressSpinnerModule,
    ProjectHeaderComponent,
    ProjectStepsComponent,
    ProjectCommentsComponent,
    MatIcon
],
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
getMaterialsLines(): string[] {
  const materials = this.project()?.materials || '';
  return materials
    .split('\n')
    .map(line => line.trim())
    .filter(line => line.length > 0);
}
}
