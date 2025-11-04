import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { ChallengeListDTO } from '../../models/challenge.model';
import { ChallengeService } from '../../services/challenge.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; 

@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
],
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.css']
})
export class ChallengesComponent implements OnInit {
  challenges: ChallengeListDTO[] = [];
  loading = true;

  constructor(private challengeService: ChallengeService) {}

  ngOnInit(): void {
    this.loadChallenges();
  }

  loadChallenges(): void {
    this.challengeService.getAllChallenge().subscribe({
      next: (data) => {
        this.challenges = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('שגיאה בטעינת אתגרים:', err);
        this.loading = false;
      }
    });
  }

  // פונקציה אחת לכל Base64
  getImageUrl(base64?: string): string {
    if (base64 && base64.trim()) {
      return `data:image/jpeg;base64,${base64}`;
    }
    return 'assets/images/default-challenge.jpg';
  }

  // עיצוב תאריך
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('he-IL', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  }
  onImageError(event: Event): void {
  const img = event.target as HTMLImageElement;
  img.src = 'assets/images/placeholder-challenge.jpg'; // או URL מקוון
}
}