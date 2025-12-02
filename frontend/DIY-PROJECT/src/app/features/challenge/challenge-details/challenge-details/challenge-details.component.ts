import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { ChallengeResponseDTO } from '../../../../models/challenge.model';
import { AuthService } from '../../../../services/auth.service';
import { ChallengeService } from '../../../../services/challenge.service';
import { ProjectCardComponent } from '../../../../shared/components/project-card/project-card/project-card.component';
import { SubmitProjectDialogComponent } from '../../../project/submit-project-dialog/submit-project-dialog/submit-project-dialog.component';

@Component({
  selector: 'app-challenge-details',
  imports: [
    ProjectCardComponent,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSpinner],
  templateUrl: './challenge-details.component.html',
  styleUrl: './challenge-details.component.css'
})
export class ChallengeDetailsComponent {
private route = inject(ActivatedRoute);
  private challengeService = inject(ChallengeService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);
  private router = inject(Router);

  challenge = signal<ChallengeResponseDTO | null>(null);
  loading = signal(true);

  isLoggedIn = computed(() => !!this.authService.currentUser());
  canSubmit = computed(() => 
    this.isLoggedIn() && this.challenge()?.status === 'OPEN'
  );

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.challengeService.getChallengeById(id).subscribe({
      next: (data) => {
        this.challenge.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  getImageUrl(): string {
    const base64 = this.challenge()?.picture;
    return base64 ? `data:image/jpeg;base64,${base64}` : 'assets/images/default-challenge.jpg';
  }

  formatDate(date: string | undefined): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('he-IL');
  }

  openSubmitDialog() {
    this.dialog.open(SubmitProjectDialogComponent, {
      width: '500px',
      data: { challengeId: this.challenge()!.id }
    });
  }
}
