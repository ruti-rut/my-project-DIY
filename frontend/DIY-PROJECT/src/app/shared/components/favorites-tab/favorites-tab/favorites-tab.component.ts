import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { ProfileService } from '../../../../services/profile.service';
import { ProjectService } from '../../../../services/project.service';
import { ToastService } from '../../../../services/toast.service';
import { ProjectCardComponent } from '../../project-card/project-card/project-card.component';

@Component({
  selector: 'app-favorites-tab',
  imports: [CommonModule, ProjectCardComponent, MatProgressSpinner],
  templateUrl: './favorites-tab.component.html',
  styleUrl: './favorites-tab.component.css'
})
export class FavoritesTabComponent {
  private profileService = inject(ProfileService);
  private toast = inject(ToastService);
  private projectService = inject(ProjectService); // הזרקת ProjectService

  favorites = signal<any[]>([]);
  loading = signal(true); 

  ngOnInit(): void {
    this.loadFavorites();
  }

  loadFavorites(): void {
    this.loading.set(true);
    this.projectService.getFavorites().subscribe({ // 👈 הקריאה לפונקציה החדשה
      next: (favs) => {
        this.favorites.set(favs);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load favorites', err);
        this.loading.set(false);
      }
    });
  }

  // הפונקציה onFavoriteRemoved נשארת כמעט זהה, רק משנה גם את ה-signal המקומי
  onFavoriteRemoved(projectId: number) {
    this.profileService.removeFavorite(projectId); // עדכון מונה ב-Header
    
    // עדכון לוקאלי של הרשימה (מחיקה מה-signal)
    this.favorites.update(favs => favs.filter(x => x.id !== projectId));
    
    this.toast.info('הוסר מהמועדפים');
  }
  
  // נעדכן את ה-computed כדי שישתמש ב-signal המקומי
  favoritesComputed = computed(() => this.favorites());
}
