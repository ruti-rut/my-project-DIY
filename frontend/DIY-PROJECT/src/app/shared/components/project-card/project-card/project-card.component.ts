import { Component, computed, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { RouterModule } from '@angular/router';
import { ProjectListDTO } from '../../../../models/project.model';
import { AvatarHelperService } from '../../../../services/avatar-helper.service';
import { FavoriteButtonComponent } from '../../favorite-button/favorite-button/favorite-button.component';
import { LikeButtonComponent } from "../../like-button/like-button.component";

@Component({
  selector: 'app-project-card',
  imports: [RouterModule,
    MatCardModule,
    FavoriteButtonComponent, LikeButtonComponent],
  templateUrl: './project-card.component.html',
  styleUrl: './project-card.component.css'
})
export class ProjectCardComponent {
  @Input({ required: true }) project!: ProjectListDTO;
  @Output() favoriteRemoved = new EventEmitter<number>();
  @Output() likeRemoved = new EventEmitter<number>();

  private avatarHelper = inject(AvatarHelperService);

  ngOnInit() {
    console.log('Project:', this.project);
    console.log('Image URL:', this.getImageUrl());
    console.log('Avatar:', this.avatar());
  }
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
    const userDto = this.project.usersSimpleDTO;

    if (!userDto) {
      // ... (קוד ברירת מחדל) ...
    }

    // 1. 🔥 שלוף את מחרוזת ה-Base64
    const base64 = userDto.profilePicture;
    const name = userDto.userName;

    // 🔥 בדיקה 1: אם יש Base64 (קדימות)
    if (base64 && base64.trim()) {
      return {
        url: `data:image/jpeg;base64,${base64}`, // בניית Data URL
        initial: '',
        color: ''
      };
    }

    // בדיקה 2: חזרה לנתיב (כגיבוי)
    const path = userDto.profilePicturePath;
    if (path) {
      return { url: `http://localhost:8080${path}`, initial: '', color: '' };
    }

    // ... (אווטאר ברירת מחדל) ...
    return {
      url: '',
      initial: this.avatarHelper.getFirstInitial(name),
      color: this.avatarHelper.generateColor(name)
    };
  });

  toggleFavorite = (isFavorited: boolean): void => {
    this.project.favorited = isFavorited;
    if (!isFavorited) {
      this.favoriteRemoved.emit(this.project.id);
    }
  };

  toggleLiked = (isLiked: boolean): void => {
    this.project.liked = isLiked;
    if (!isLiked) {
      this.likeRemoved.emit(this.project.id);
    }
  };
  // בדיוק כמו onImageError באתגרים!
  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = '/assets/default-project.jpg';
  }
}