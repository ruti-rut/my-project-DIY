// src/app/services/profile.service.ts
import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ProjectListDTO } from '../models/project.model';
import { UserProfileDTO } from '../models/user.model';
import { ToastService } from './toast.service';


@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);
  private toast = inject(ToastService);

  profile = signal<UserProfileDTO | null>(null);
  loading = signal(true);

  loadProfile() {
    this.loading.set(true);
    this.http.get<UserProfileDTO>('http://localhost:8080/api/users/profile').subscribe({
      next: (data) => {
        console.log('Profile loaded:', data); // לדיבוג
        this.profile.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading profile:', err);
        this.toast.error('שגיאה בטעינת הפרופיל');
        this.loading.set(false);
      }
    });
  }

  removeFavorite(projectId: number) {
    this.profile.update(p => {
      if (!p) return null;
      return {
        ...p,
        favoritesCount: p.favoritesCount - 1
      };
    });
  }

  deleteMyProject(projectId: number) {
    this.profile.update(p => {
      if (!p) return null;
      return {
        ...p,
        // 🔥 מחקנו את השורה הזו:
        // myProjects: p.myProjects.filter(x => x.id !== projectId), 
        projectsCount: p.projectsCount - 1
      };
    });
    this.toast.success('הפרויקט נמחק בהצלחה');
  }
  updateProfile(city: string, aboutMe: string, file: File | null) {
  const formData = new FormData();
  formData.append('city', city || '');
  formData.append('aboutMe', aboutMe || '');
  if (file) {
    formData.append('file', file, file.name);
  }

  this.loading.set(true);

  // ✅ הסר את withCredentials: false כדי לאפשר שליחת קוקי האימות
  return this.http.post<UserProfileDTO>('http://localhost:8080/api/users/me/update-profile', formData);
}
  
  }