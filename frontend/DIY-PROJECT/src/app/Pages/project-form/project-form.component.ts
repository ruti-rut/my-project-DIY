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

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private projectService = inject(ProjectService);
  private stepService = inject(StepService);
  private categoryService = inject(CategoryService);

  form = signal<FormGroup>(this.createEmptyForm());
  isEditMode = signal(false);
  loading = signal(false);

  categories = signal<Category[]>([]);
  tags = signal<string[]>([]);
  coverPreview = signal<string | null>(null);
  stepPreviews = signal<(string | null)[]>([]);

  steps = computed(() => this.form().get('steps') as FormArray);

  ngOnInit() {
    this.loadCategories();

    if (this.projectId) {
      this.isEditMode.set(true);
      this.loadProjectForEdit();
    } else {
      this.isEditMode.set(false);
      this.addStep();
      this.stepPreviews.update(prev => [...prev, null]); // ✅ הוספת preview ריק לשלב הראשון במצב יצירה חדש
    }
  }

  private createEmptyForm(): FormGroup {
    return this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      materials: [''],
      categoryId: [null, Validators.required],
      ages: [''],
      timePrep: [''],
      tagNames: [[]],
      picture: [null as File | null],
      isDraft: [true],
      steps: this.fb.array([], Validators.minLength(1))
    });
  }

  private loadCategories() {
    this.categoryService.getAllCategories().subscribe(cats => this.categories.set(cats));
  }

 private loadProjectForEdit() {
  this.loading.set(true);
  this.projectService.getById(this.projectId!).subscribe({
    next: (project: Project) => {
      // איפוס מלא של הטופס
      this.form.set(this.createEmptyForm());

      this.form().patchValue({
        title: project.title,
        description: project.description,
        materials: project.materials || '',
        categoryId: project.category?.id,
        ages: project.ages || '',
        timePrep: project.timePrep || '',
        isDraft: project.isDraft
      });

      // תגיות
      this.tags.set(project.tags?.map(t => t.name) || []);

      // תמונת שער – Base64 או null
      if (project.picture) {
        const fullCoverUrl = project.picture.startsWith('data:')
          ? project.picture
          : `data:image/jpeg;base64,${project.picture}`;
        this.coverPreview.set(fullCoverUrl);
      } else {
        this.coverPreview.set(null);
      }

      // שלבים + תמונות שלהם
      this.steps().clear();
      this.stepPreviews.set([]);

     project.steps?.forEach((step) => {
        this.addStep(step);

        // 1. הגדרת משתנה לתמונה
        let stepPreviewUrl: string | null = null;
        
        // 2. ✅ הוספת התיקון: בדיקת קידומת Base64
        if (step.picture) {
          stepPreviewUrl = step.picture.startsWith('data:')
            ? step.picture
            : `data:image/jpeg;base64,${step.picture}`;
        }
        
        // 3. עדכון הסיגנל
        this.stepPreviews.update(prev => [...prev, stepPreviewUrl]);
      });

      this.loading.set(false);
    },
    error: () => {
      this.loading.set(false);
      alert('שגיאה בטעינת הפרויקט');
      this.router.navigate(['/profile']);
    }
  });
}

  addStep(existing?: any) {
    const step = this.fb.group({
      title: [existing?.title || '', Validators.required],
      content: [existing?.content || '', Validators.required],
      picture: [null as File | null]
    });
    this.steps().push(step);
  }

  removeStep(i: number) {
    if (this.steps().length <= 1) return;
    this.steps().removeAt(i);
    this.stepPreviews.update(prev => prev.filter((_, idx) => idx !== i));
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
      this.tags.update(t => [...t, value]);
    }
    event.chipInput!.clear();
  }

  removeTag(tag: string) {
    this.tags.update(t => t.filter(x => x !== tag));
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
      challengeId: this.challengeId || undefined,
      ages: formValue.ages,
      timePrep: formValue.timePrep,
      tagNames: this.tags(),
      draft: isDraft
    };

    const formData = new FormData();
    formData.append('project', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (formValue.picture) {
      formData.append('image', formValue.picture);
    }

    const request$ = this.isEditMode()
      ? this.projectService.updateProject(this.projectId!, formData)
      : this.projectService.uploadProject(formData);

    request$.subscribe({
      next: (savedProject) => {
        const stepObs = formValue.steps.map((s: any, i: number) => {
          const step: StepDTO = { ...s, stepNumber: i + 1, projectId: savedProject.id };
          const fd = new FormData();
          fd.append('step', new Blob([JSON.stringify(step)], { type: 'application/json' }));
          if (s.picture) fd.append('image', s.picture);
          return this.stepService.uploadStep(fd);
        });

        forkJoin(stepObs).subscribe({
          next: () => {
            this.router.navigate(['/project', savedProject.id]);
          },
          error: () => this.loading.set(false)
        });
      },
      error: () => this.loading.set(false)
    });
  }
}