import { Component, inject, Input, signal } from '@angular/core';
import { CommentService } from '../services/comment.service';
import { CommentDTO } from '../models/comment.model';
import { CommonModule } from '@angular/common';
import { CommentFormComponent } from '../comment-form/comment-form.component';
import { AvatarHelperService } from '../services/avatar-helper.service';

@Component({
  selector: 'app-project-comments',
  imports: [CommonModule, CommentFormComponent],
  templateUrl: './project-comments.component.html',
  styleUrl: './project-comments.component.css'
})
export class ProjectCommentsComponent {
@Input({ required: true }) projectId!: number;

  loading = signal(true);
  comments = signal<CommentDTO[]>([]);

  private commentService = inject(CommentService);
  private avatarHelper = new AvatarHelperService();

  ngOnInit() {
    this.loadComments();
  }

  loadComments() {
    this.loading.set(true);
    this.commentService.getComments(this.projectId).subscribe({
      next: (comments) => {
        this.comments.set(comments);
        this.loading.set(false);
      },
      error: () => {
        this.comments.set([]);
        this.loading.set(false);
      }
    });
  }

  onCommentAdded() {
    this.loadComments();
  }

  getInitial(name: string): string {
    return this.avatarHelper.getFirstInitial(name || '?');
  }

  getColor(name: string): string {
    return this.avatarHelper.generateColor(name || '?');
  }
}