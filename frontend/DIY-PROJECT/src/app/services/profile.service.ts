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

  /**
   * טוען את הפרופיל המלא של המשתמש הנוכחי
   */
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

  /**
   * מוחק פרויקט מהמועדפים - עדכון לוקאלי
   */
  removeFavorite(projectId: number) {
    this.profile.update(p => {
      if (!p) return null;
      return {
        ...p,
        favoriteProjects: p.favoriteProjects.filter(x => x.id !== projectId),
        favoritesCount: p.favoritesCount - 1
      };
    });
  }

  /**
   * מוחק פרויקט שלי - עדכון לוקאלי
   */
  deleteMyProject(projectId: number) {
    this.profile.update(p => {
      if (!p) return null;
      return {
        ...p,
        myProjects: p.myProjects.filter(x => x.id !== projectId),
        projectsCount: p.projectsCount - 1
      };
    });
    this.toast.success('הפרויקט נמחק בהצלחה');
  }

}