import { Component, Input } from '@angular/core';
import { StepResponse } from '../../../../models/step.model';

@Component({
  selector: 'app-project-steps',
  imports: [],
  templateUrl: './project-steps.component.html',
  styleUrl: './project-steps.component.css'
})
export class ProjectStepsComponent {
  @Input({ required: true }) steps!: StepResponse[];
}
