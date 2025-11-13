import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProjectFormComponent } from '../pages/project-form/project-form.component';
@Component({
  selector: 'app-project-create',
  standalone: true,
  imports: [ProjectFormComponent], // ← עכשיו תקין!
  templateUrl: './project-create.component.html',
  styleUrl: './project-create.component.css'
})
export class ProjectCreateComponent {
  private route = inject(ActivatedRoute);
  challengeId: number | null = null;

  constructor() {
    const ch = this.route.snapshot.queryParamMap.get('challenge');
    this.challengeId = ch ? +ch : null; // ← +ch = המרה ל-number
  }
}
