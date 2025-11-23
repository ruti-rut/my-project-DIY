import { Component, computed, inject, Input, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Category, Tag } from '../../models/category.model';
import { Project, ProjectCreateDTO } from '../../models/project.model';
import { StepDTO } from '../../models/step.model';
import { AuthService } from '../../services/auth.service';
import { CategoryService } from '../../services/category.service';
import { ProjectService } from '../../services/project.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';
import { StepService } from '../../services/step.service';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatChipsModule, MatProgressSpinnerModule],
  templateUrl: './project-form.html',
  styleUrl: './project-form.css'
})
export class ProjectFormComponent implements OnInit {
  @Input() projectId: number | null = null;
  @Input() challengeId: number | null = null;

  // === DI ===
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private projectService = inject(ProjectService);
  private stepService = inject(StepService);

  private categoryService = inject(CategoryService);
  private authService = inject(AuthService);

  // === Signals ===
  form = signal<FormGroup>(this.createForm());
  isEditMode = signal(false);
  loading = signal(false);

  categories = signal<Category[]>([]);
  tags = signal<string[]>([]);
  coverPreview = signal<string | null>(null);
  stepPreviews = signal<(string | null)[]>([]);

  steps = computed(() => this.form().get('steps') as FormArray);

  ngOnInit() {
    this.loadCategories();

    // קבע מצב עריכה לפי @Input
    if (this.projectId) {
      this.isEditMode.set(true);
      this.loadProject(this.projectId);
    } else {
      this.isEditMode.set(false);
      this.addStep();
    }
  }

  private createForm(): FormGroup {
    const form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      materials: [''],
      categoryId: [null, Validators.required],
      ages: [''],
      timePrep: [''],
      tagNames: [[]],
      picture: [null],
      isDraft: [true],
      steps: this.fb.array([], Validators.minLength(1))
    });
    return form;
  }

  private loadCategories() {
    this.categoryService.getAllCategories().subscribe(cats => this.categories.set(cats));
  }

  private loadProject(id: number) {
    this.loading.set(true);
    this.projectService.getById(id).subscribe({
      next: (project: Project) => {
        this.form().patchValue({
          title: project.title,
          description: project.description,
          materials: project.materials,
          categoryId: project.category?.id,
          ages: project.ages,
          timePrep: project.timePrep,
          isDraft: project.isDraft
        });

        this.tags.set(project.tags?.map(t => t.name) || []);
        this.coverPreview.set(project.picture ? `/assets/images/projects/${project.picture}` : null);

        this.steps().clear();
        this.stepPreviews.set([]);
        project.steps?.forEach(step => {
          this.addStep(step);
          const i = this.steps().length - 1;
          this.stepPreviews.set([
            ...this.stepPreviews(),
            step.picture ? `/assets/images/steps/${step.picture}` : null
          ]);
        });
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  addStep(existing?: any) {
    const step = this.fb.group({
      title: [existing?.title || '', Validators.required],
      content: [existing?.content || '', Validators.required],
      picture: [null]
    });
    this.steps().push(step);
    this.stepPreviews.set([...this.stepPreviews(), null]);
  }

  removeStep(i: number) {
    if (this.steps().length > 1) {
      this.steps().removeAt(i);
      this.stepPreviews.set(this.stepPreviews().filter((_, index) => index !== i));
    }
  }

  onCoverChange(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.form().get('picture')?.setValue(file);
      this.coverPreview.set(URL.createObjectURL(file));
    }
  }

  onStepImageChange(event: Event, i: number) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.steps().at(i).get('picture')?.setValue(file);
      const newPreviews = [...this.stepPreviews()];
      newPreviews[i] = URL.createObjectURL(file);
      this.stepPreviews.set(newPreviews);
    }
  }

  addTag(event: MatChipInputEvent) {
    const value = (event.value || '').trim();
    if (value && this.tags().length < 5 && !this.tags().includes(value)) {
      this.tags.set([...this.tags(), value]);
      this.form().get('tagNames')?.setValue(this.tags());
    }
    event.chipInput!.clear();
  }

  removeTag(tag: string) {
    this.tags.set(this.tags().filter(t => t !== tag));
    this.form().get('tagNames')?.setValue(this.tags());
  }

  submit(isDraft: boolean) {
    if (this.form().invalid) {
      this.form().markAllAsTouched();
      return;
    }

    this.loading.set(true);
    const formValue = this.form().value;

    const data: ProjectCreateDTO = {
      title: formValue.title,
      description: formValue.description,
      materials: formValue.materials,
      categoryId: formValue.categoryId,
      challengeId: this.challengeId,
      ages: formValue.ages,
      timePrep: formValue.timePrep,
      tagNames: this.tags(),  // ← נשלח!
      isDraft                 // ← לפי כפתור!
    };

    const formData = new FormData();
    formData.append('project', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (formValue.picture) formData.append('image', formValue.picture, formValue.picture.name);

    this.projectService.uploadProject(formData).subscribe({
      next: (saved) => {
        const steps = formValue.steps;
        const obs = steps.map((s: any, i: number) => {
          const step: StepDTO = { ...s, stepNumber: i + 1, projectId: saved.id };
          const fd = new FormData();
          fd.append('step', new Blob([JSON.stringify(step)], { type: 'application/json' }));
          if (s.picture) fd.append('image', s.picture, s.picture.name);
          return this.stepService.uploadStep(fd);
        });

        forkJoin(obs).subscribe({
          next: () => this.router.navigate(['/project', saved.id]),
          error: () => this.loading.set(false)
        });
      },
      error: () => this.loading.set(false)
    });
  }
}