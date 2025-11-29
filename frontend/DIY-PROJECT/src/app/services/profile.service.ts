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
    this.http.get<UserProfileDTO>('/api/project/profile').subscribe({
      next: (data) => {
        this.profile.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.toast.error('שגיאה בטעינת הפרופיל');
        this.loading.set(false);
      }
    });
  }

  removeFavorite(projectId: number) {
    this.profile.update(p => p ? {
      ...p,
      favoriteProjects: p.favoriteProjects.filter(x => x.id !== projectId),
      favoritesCount: p.favoritesCount - 1
    } : null);
  }

  deleteMyProject(projectId: number) {
    this.profile.update(p => p ? {
      ...p,
      myProjects: p.myProjects.filter(x => x.id !== projectId),
      projectsCount: p.projectsCount - 1
    } : null);
    this.toast.success('הפרויקט נמחק בהצלחה');
  }
}