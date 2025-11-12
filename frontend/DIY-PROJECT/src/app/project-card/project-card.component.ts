import { Component, computed, inject, Input, signal } from '@angular/core';
import { ProjectListDTO } from '../models/project.model';
import { MatCardModule } from '@angular/material/card';
import { RouterModule } from '@angular/router';
import { FavoriteButtonComponent } from '../favorite-button/favorite-button.component';
import { AvatarHelperService } from '../services/avatar-helper.service';

@Component({
  selector: 'app-project-card',
  imports: [RouterModule,
    MatCardModule,
    FavoriteButtonComponent],
  templateUrl: './project-card.component.html',
  styleUrl: './project-card.component.css'
})
export class ProjectCardComponent {
@Input({ required: true }) project!: ProjectListDTO;

  private avatarHelper = inject(AvatarHelperService);

  // בדיוק כמו getImageUrl באתגרים!
getImageUrl(): string {
  const base64 = this.project.picture;
  if (base64 && base64.trim()) {
    return `data:image/jpeg;base64,${base64}`;
  }
  return 'https://picsum.photos/400/300?random=' + this.project.id;
}
  // אווטאר – נשאר כמו שהיה
  avatar = computed(() => {
    const path = this.project.usersSimpleDTO.profilePicturePath;
    const name = this.project.usersSimpleDTO.userName;

    if (path) {
      return { url: `http://localhost:8080${path}`, initial: '', color: '' };
    }

    return {
      url: '',
      initial: this.avatarHelper.getFirstInitial(name),
      color: this.avatarHelper.generateColor(name)
    };
  });

  toggleFavorite = (isFavorited: boolean): void => {
    this.project.isFavorited = isFavorited;
  };

  // בדיוק כמו onImageError באתגרים!
  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = '/assets/default-project.jpg';
  }
}