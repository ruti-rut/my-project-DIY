import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProjectFormComponent } from '../../features/project/project-form/project-form.component';
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

ngOnInit(): void {
  const challengeId = this.route.snapshot.queryParamMap.get('challengeId');
  this.challengeId = challengeId ? Number(challengeId) : null;
}
}
