import { Component, computed, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommentService } from '../services/comment.service';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { CommentCreateDTO } from '../models/comment.model';
import { AuthService } from '../services/auth.service';
import { AvatarHelperService } from '../services/avatar-helper.service';

@Component({
  selector: 'app-comment-form',
  imports: [FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatProgressSpinner],
  templateUrl: './comment-form.component.html',
  styleUrl: './comment-form.component.css'
})
export class CommentFormComponent {
  @Input({ required: true }) projectId!: number;
  @Output() commentAdded = new EventEmitter<void>();

  newComment = '';

  private commentService = inject(CommentService);
  private auth = inject(AuthService);
  private avatarHelper = inject(AvatarHelperService);

  currentUser = this.auth.currentUser;
  avatarInitial = computed(() => this.avatarHelper.getFirstInitial(this.currentUser()?.userName || '?'));
  avatarColor = computed(() => this.avatarHelper.generateColor(this.currentUser()?.userName || '?'));

  userAvatarUrl = computed(() => {
    const user = this.currentUser();
    if (!user) return null;

    // 1. קדימות ל-Base64 (אם יש לך שדה כזה ב-Users model)
    // אם ה-Base64 נמצא בשדה profilePicture:
    if (user.profilePicture && user.profilePicture.startsWith('data:image')) {
      return user.profilePicture;
    }
    if (user.profilePicture && user.profilePicture.trim()) {
      return `data:image/jpeg;base64,${user.profilePicture}`;
    }

    // 2. חזרה לנתיב (אם אין Base64)
    
    return null;
  });


  submit() {
    if (!this.newComment.trim()) return;

    const dto: CommentCreateDTO = {
      content: this.newComment,
      projectId: this.projectId
    };

    this.commentService.addComment(dto).subscribe({
      next: () => {
        this.newComment = '';
        this.commentAdded.emit();
      },
      error: (err) => console.error('שגיאה', err)
    });
  }
}