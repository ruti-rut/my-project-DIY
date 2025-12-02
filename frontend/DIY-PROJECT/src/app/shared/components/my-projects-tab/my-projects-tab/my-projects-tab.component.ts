import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProjectCardComponent } from '../../project-card/project-card/project-card.component';
import { ProfileService } from '../../../../services/profile.service';
import { ProjectService } from '../../../../services/project.service';

@Component({
  selector: 'app-my-projects-tab',
  imports: [CommonModule,
    ProjectCardComponent,
    MatIconModule,
    MatButtonModule,
    RouterModule,
    MatIconModule],
  templateUrl: './my-projects-tab.component.html',
  styleUrl: './my-projects-tab.component.css'
})
export class MyProjectsTabComponent {
  private profileService = inject(ProfileService);
  private projectService = inject(ProjectService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar)
  myProjects = signal<any[]>([]);
  loading = signal(true);

  challengeIdToAssign = signal<number | null>(null);
  ngOnInit(): void {
    // 🌟 בדוק אם challengeId הועבר כפרמטר ב-URL
    this.route.queryParams.subscribe(params => {
      const id = params['challengeId'];
      if (id) {
        this.challengeIdToAssign.set(Number(id));
      } else {
        this.challengeIdToAssign.set(null);
      }
    });

    this.loadMyPublishedProjects();
  }

  loadMyPublishedProjects(): void {
    this.loading.set(true);
    this.projectService.getMyPublishedProjects().subscribe({
      next: (projects) => {
        this.myProjects.set(projects);
        this.loading.set(false);

        // 🔥 הערה חשובה: פונקציה זו מחזירה רק רשימה, לא מעדכנת את ה-ProfileService
        // אם תרצי שהמונה ב-Header יתעדכן, ה-ProfileService צריך לעשות Get לפרופיל מחדש.
      },
      error: (err) => {
        console.error('Failed to load my projects', err);
        this.loading.set(false);
      }
    });
  }


  deleteProject(projectId: number) {
    if (!confirm('למחוק את הפרויקט לצמיתות? לא ניתן לשחזר!')) return;

    // 🔥 שימוש ב-ProjectService
    this.projectService.deleteProject(projectId).subscribe({
      next: () => {
        // עדכון לוקאלי של הרשימה (מחיקה מה-signal)
        this.myProjects.update(projects => projects.filter(x => x.id !== projectId));
        // עדכון מונה ב-Header
        this.profileService.deleteMyProject(projectId);
      },
      error: () => alert('שגיאה במחיקה')
    });
  }
  //  assignProject(projectId: number): void {
  //       const challengeId = this.challengeIdToAssign();
  //       if (!challengeId) return;

  //       this.projectService.assignProjectToChallenge(projectId, challengeId).subscribe({
  //           next: () => {
  //               alert('הפרויקט שויך בהצלחה!');

  //               // 1. הסר את הפרויקט המשויך מהרשימה הנוכחית (כי הוא כבר לא זמין לשיוך)
  //               this.myProjects.update(projects => projects.filter(p => p.id !== projectId));

  //               // 2. ניווט חזרה לדף האתגר
  //               this.router.navigate(['/challenge', challengeId]);
  //           },
  //           error: () => {
  //               alert('שגיאה בשיוך הפרויקט.');
  //           }
  //       });
  //   }
  // נעדכן את ה-computed כדי שישתמש ב-signal המקומי
  myProjectsComputed = computed(() => this.myProjects());

  assignProject(projectId: number): void {
    const challengeId = this.challengeIdToAssign();
    if (!challengeId) return;

    this.projectService.assignProjectToChallenge(projectId, challengeId).subscribe({
      next: () => {
        this.snackBar.open('הפרויקט שויך בהצלחה!', 'סגור', {
          duration: 4000,
          panelClass: ['success-snackbar']
        });

        this.myProjects.update(projects => projects.filter(p => p.id !== projectId));
        this.router.navigate(['/challenge', challengeId]);
      },

      error: (err: HttpErrorResponse) => {
        console.error('שגיאה בשיוך פרויקט:', err); // ← חשוב! תראה בקונסולה בדיוק מה חוזר

        let message = 'שגיאה בשיוך הפרויקט.';

        // לוקחים את כל מה שיש – לא משנה השם של השדה
        const errorBody = err.error;
        const rawMessage = typeof errorBody === 'string'
          ? errorBody
          : (errorBody?.reason || errorBody?.message || errorBody?.error || err.message || '');

        const msg = rawMessage.toString().toLowerCase();

        // === כל המקרים השכיחים ביותר ===
        if (msg.includes('already') || msg.includes('כבר') || msg.includes('קיים')) {
          if (msg.includes('user') || msg.includes('משתמש') || msg.includes('submit') || msg.includes('הגיש')) {
            message = 'כבר שלחת פרויקט לאתגר זה!\nמשתמש יכול להגיש פרויקט אחד בלבד.';
          } else {
            message = 'הפרויקט כבר משויך לאתגר אחר.';
          }
        }
        else if (err.status === 404) {
          message = 'הפרויקט או האתגר לא נמצאו.';
        }
        else if (err.status === 403) {
          message = 'אין לך הרשאה לשייך פרויקט זה.';
        }
        else if (err.status === 400) {
          // אם זה 400 אבל לא תפסנו קודם – נראה את ההודעה כמו שהיא (אבל בעברית אם אפשר)
          message = 'לא ניתן לשייך את הפרויקט.\nייתכן שכבר הגשת פרויקט לאתגר זה.';
        }
        else {
          message = 'שגיאה בשרת. נסה שוב מאוחר יותר.';
        }

        this.snackBar.open(message, 'סגור', {
          duration: 9000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}
