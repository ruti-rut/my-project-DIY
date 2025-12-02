import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { CommonModule } from '@angular/common';
import { ChallengeCreateDTO, Challenge } from '../../../models/challenge.model';
import { ChallengeService } from '../../../services/challenge.service';

@Component({
  selector: 'app-create-challenge',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './add-challenge.component.html',
  styleUrls: ['./add-challenge.component.css']
})
export class AddChallengeComponent {
  challengeForm: FormGroup;
  previewImage = signal<string | null>(null);
  selectedFile: File | null = null;
  isSubmitting = signal(false);

  constructor(
    private fb: FormBuilder,
    private challengeService: ChallengeService
  ) {
    this.challengeForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      theme: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => this.previewImage.set(reader.result as string);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit(): void {
    if (this.challengeForm.invalid || !this.selectedFile || this.isSubmitting()) return;

    this.isSubmitting.set(true);

    const dto: ChallengeCreateDTO = this.challengeForm.value;
    const formData = new FormData();

    // חלק 1: DTO כ-JSON
    formData.append('challenge', new Blob([JSON.stringify(dto)], { type: 'application/json' }));

    // חלק 2: תמונה
    formData.append('image', this.selectedFile, this.selectedFile.name);

    this.challengeService.createChallenge(formData).subscribe({
      next: (challenge: Challenge) => {
        alert(`האתגר נוצר! ID: ${challenge.id}`);
        this.resetForm();
      },
      error: (err) => {
        console.error(err);
        alert('שגיאה בשליחה');
        this.isSubmitting.set(false);
      }
    });
  }

  private resetForm(): void {
    this.challengeForm.reset();
    this.previewImage.set(null);
    this.selectedFile = null;
    this.isSubmitting.set(false);
  }
}