import { Component, effect, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterModule } from '@angular/router';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { AvatarHelperService } from '../../../../services/avatar-helper.service';
import { ProfileService } from '../../../../services/profile.service';
import { ToastService } from '../../../../services/toast.service';

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
  private profileService = inject(ProfileService);
  private avatarHelper = inject(AvatarHelperService);
  private toast = inject(ToastService);
  private router = inject(Router);

  loading = signal(false);
  previewUrl = signal<string>('');
  profile = this.profileService.profile;

  form = this.fb.group({
    city: [''],
    aboutMe: [''],
    profilePicture: [null as File | null]
  });

  constructor() {
    this.profileService.loadProfile(); // טוען את הפרופיל מיד

    effect(() => {
      const p = this.profileService.profile();
      if (!p?.userName) return;

      // מילוי הטופס
      this.form.patchValue({
        city: p.city || '',
        aboutMe: p.aboutMe || ''
      });

      // תמונת הפרופיל – את עדיין מחזירה base64 אז זה בטוח עובד
      if (p.profilePicture) {
        const url = p.profilePicture.startsWith('data:')
          ? p.profilePicture
          : `data:image/jpeg;base64,${p.profilePicture}`;

        this.previewUrl.set(url);           // ← זה יציג את התמונה הישנה
      } else {
        // fallback לאווטאר
        const color = this.avatarHelper.generateColor(p.userName);
        const initial = this.avatarHelper.getFirstInitial(p.userName);
        this.previewUrl.set(
          `https://ui-avatars.com/api/?name=${initial}&background=${color.substring(1)}&color=fff&size=200`
        );
      }
    });
  }
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.form.patchValue({ profilePicture: file });
      // הצגת התמונה החדשה שבחר המשתמש
      this.previewUrl.set(URL.createObjectURL(file));
    }
  }

  save() {
    if (this.form.invalid) return;

    // ... הנתונים המלאים כבר מועברים כפי שצריך
    const city = this.form.value.city ?? '';
    const aboutMe = this.form.value.aboutMe ?? '';
    const file = this.form.value.profilePicture ?? null;

    this.profileService.loading.set(true); // עדכון ה-loading לפני הבקשה

    this.profileService.updateProfile(city, aboutMe, file).subscribe({
      next: data => {
        this.profileService.profile.set(data);
        this.toast.success('הפרופיל עודכן בהצלחה!');
        this.router.navigate(['/profile']);

      },
      error: err => {
        console.error(err);
        this.toast.error('שגיאה בעדכון הפרופיל');
        this.router.navigate(['/profile']);

      },
      complete: () => this.profileService.loading.set(false)
    });
  }

  cancel() {
    history.back();
  }
}



