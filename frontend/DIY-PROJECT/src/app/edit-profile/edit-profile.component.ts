import { Component, inject, signal } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AvatarHelperService } from '../services/avatar-helper.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { MatProgressSpinner } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-edit-profile',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterModule, MatProgressSpinner],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent {
private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private profileService = inject(ProfileService);
  private avatarHelper = inject(AvatarHelperService);

  profile = this.profileService.profile;
  loading = signal(false);

  // תצוגה מקדימה של התמונה
  previewUrl = signal<string>('');

  form = this.fb.group({
    city: [''],
    aboutMe: [''],
    profilePicture: [null as File | null]
  });

  ngOnInit() {
    const p = this.profile();
    if (p) {
      this.form.patchValue({
        city: p.city || '',
        aboutMe: p.aboutMe || ''
      });

      if (p.profilePicturePath) {
        this.previewUrl.set(`http://localhost:8080/images/${p.profilePicturePath}`);
      } else {
        // אווטאר ברירת מחדל אם אין תמונה
        const color = this.avatarHelper.generateColor(p.userName);
        const initial = this.avatarHelper.getFirstInitial(p.userName);
        this.previewUrl.set(`https://ui-avatars.com/api/?name=${initial}&background=${color.substring(1)}&color=fff&size=200`);
      }
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.form.patchValue({ profilePicture: file });
      this.previewUrl.set(URL.createObjectURL(file));
    }
  }

  save() {
    if (this.form.invalid) return;

    this.loading.set(true);
    const formData = new FormData();

    formData.append('city', this.form.value.city || '');
    formData.append('aboutMe', this.form.value.aboutMe || '');

    const file = this.form.value.profilePicture;
    if (file) {
      formData.append('file', file, file.name);
    }

    this.http.put('/api/users/me/update-profile', formData).subscribe({
      next: () => {
        alert('הפרופיל עודכן בהצלחה!');
        this.profileService.loadProfile(); // רענון המידע בפרופיל
        // אפשר גם לנווט חזרה: this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error(err);
        alert('שגיאה בעדכון הפרופיל');
      },
      complete: () => this.loading.set(false)
    });
  }

  cancel() {
    history.back();
  }
}
