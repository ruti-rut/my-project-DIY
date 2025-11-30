import { Component, computed, inject } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { CommonModule } from '@angular/common';
import { ProjectCardComponent } from '../project-card/project-card.component';
import { MatIcon } from '@angular/material/icon';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-favorites-tab',
  imports: [CommonModule, ProjectCardComponent,MatIcon],
  templateUrl: './favorites-tab.component.html',
  styleUrl: './favorites-tab.component.css'
})
export class FavoritesTabComponent {
private profileService = inject(ProfileService);
  private toast = inject(ToastService);

  favorites = computed(() => this.profileService.profile()?.favoriteProjects ?? []);

  onFavoriteRemoved(projectId: number) {
    this.profileService.removeFavorite(projectId);
    this.toast.info('הוסר מהמועדפים');
  }}
