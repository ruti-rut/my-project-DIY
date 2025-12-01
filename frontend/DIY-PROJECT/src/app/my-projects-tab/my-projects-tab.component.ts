import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { ProjectCardComponent } from '../project-card/project-card.component';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { ProjectService } from '../services/project.service';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-my-projects-tab',
  imports: [CommonModule,
    ProjectCardComponent,
    MatIconModule,
    MatButtonModule,
    RouterModule,
    MatIconModule, MatProgressSpinner],
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

    this.loadMyProjects();
  }

  loadMyProjects(): void {
    this.loading.set(true);
    this.projectService.getMyProjects().subscribe({
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
      // 🌟 שימוש ב-SnackBar במקום alert להצלחה
      this.snackBar.open('הפרויקט שויך בהצלחה!', 'סגור', { duration: 3000 });
      
      // 1. הסר את הפרויקט המשויך מהרשימה הנוכחית
      this.myProjects.update(projects => projects.filter(p => p.id !== projectId));
      
      // 2. ניווט חזרה לדף האתגר
      this.router.navigate(['/challenge', challengeId]);
    },
    error: (err: HttpErrorResponse) => { // 🌟 הגדרת ה-error כ-HttpErrorResponse לגישה קלה
      let errorMessage = 'שגיאה בשיוך הפרויקט לאתגר.';

      // 🌟🌟🌟 טיפול בשגיאת המגבלה 🌟🌟🌟
      // כאשר ה-Backend זורק ResponseStatusException, גוף התגובה מכיל את השגיאה
      if (err.status === 400 && err.error && err.error.reason) {
          // err.error.reason מכיל את ההודעה: "User already submitted a project to this challenge."
          const backendReason = err.error.reason;
          
          // אנו משווים את ההודעה הנשלחת מה-Backend ומציגים הודעה בעברית
          if (backendReason.includes('User already submitted')) {
             errorMessage = 'משתמש יכול לשייך **פרויקט אחד בלבד** לאתגר זה.';
          } else {
             // אם זו שגיאת 400 אחרת, נציג את ההודעה של ה-Backend
             errorMessage = backendReason; 
          }
          
      } else if (err.status === 404) {
          errorMessage = 'הפרויקט או האתגר לא נמצאו.';
      } else {
          // שגיאות שרת כלליות (500) או שגיאות לא צפויות
          errorMessage = `שגיאת שרת: ${err.statusText || 'לא ידועה'}`;
      }
      
      // 🛑 שימוש ב-SnackBar במקום alert לשגיאה
      this.snackBar.open(errorMessage, 'סגור', { 
          duration: 7000, 
          panelClass: ['error-snackbar'] // ניתן להשתמש ב-CSS זה כדי לעצב את השגיאה
      });
    }
  });
}
}

