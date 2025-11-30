import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectFormComponent } from '../pages/project-form/project-form.component';

@Component({
  selector: 'app-project-edit',
  imports: [CommonModule, ProjectFormComponent, MatProgressSpinnerModule],
  templateUrl: './project-edit.component.html',
  styleUrl: './project-edit.component.css'
})
export class ProjectEditComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  projectId: number | null = null;
  loading = true;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id || isNaN(+id)) {
      alert('פרויקט לא נמצא');
      this.router.navigate(['/profile']);
      return;
    }
    this.projectId = +id;
    this.loading = false;
  }
}
