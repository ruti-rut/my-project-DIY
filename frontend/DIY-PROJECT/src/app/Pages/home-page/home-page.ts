import { Component, OnInit, inject, signal } from '@angular/core';
import { HomePageService } from '../../services/homePage.service';
import { CommonModule, KeyValue } from '@angular/common'; // 🔥 הוספנו CommonModule ו-KeyValue
import { RouterLink } from '@angular/router'; // בשביל routerLink
import { MatGridListModule } from '@angular/material/grid-list'; // מודול לרשימת פרויקטים
import { MatCardModule } from '@angular/material/card'; // מודול לכרטיסיות אתגרים
import { MatIconModule } from '@angular/material/icon'; // אייקונים
import { MatButtonModule } from '@angular/material/button'; // כפתורים
import { CategoryService } from '../../services/category.service';
import { Category } from '../../models/category.model';
import { HomeResponseDTO } from '../../models/home-page.model';
import { ProjectListDTO } from '../../models/project.model';
import { ProjectCardComponent } from '../../project-card/project-card.component';
import { MatSpinner } from '@angular/material/progress-spinner';
import { ChallengeListDTO } from '../../models/challenge.model';

@Component({
  selector: 'app-home-page',
  standalone: true,
  // ✅ הוספת המודולים הנדרשים ל-Material ולוגיקה
  imports: [
    CommonModule, RouterLink, MatGridListModule,
    MatCardModule, MatIconModule, MatButtonModule, MatSpinner,
    ProjectCardComponent // הקומפוננטה של הפרויקט
  ],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css'
})
export class HomePageComponent implements OnInit {

  private homeService = inject(HomePageService);
  private categoryService = inject(CategoryService);

  homeData: HomeResponseDTO | null = null;
  isLoading: boolean = true;
  // 💡 מפה לשמות הקטגוריות: { 1: "בנייה", 2: "מטבח"... }
  categoryNames: Record<number, string> = {};
  // משתנה עזר ל-HTML (כדי לקבל את המפה בצורה נגישה)
  projectsMap: KeyValue<number, ProjectListDTO[]>[] = [];
  challengeMap:  ChallengeListDTO[] = []


  ngOnInit(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (categories: Category[]) => {
        // 2. יוצרים מפה: {ID: Name}
        categories.forEach(cat => {
          // שימוש ב-cat.id כ-Key (מספר), ו-cat.name כ-Value (שם)
          this.categoryNames[cat.id!] = cat.name;
        })
      }
    })
    this.loadHomeData();
  }

  loadHomeData(): void {
    this.isLoading = true;
    this.homeService.getHomeData().subscribe({
      next: (data: HomeResponseDTO) => {
        this.homeData = data;
        this.isLoading = false;

        // המרת המפה לרשימה של KeyValue לצורך לולאה ב-HTML
        this.projectsMap = Object.keys(data.projectsPerCategory).map(key => ({
          key: parseInt(key),
          value: data.projectsPerCategory[parseInt(key)]
        })).sort((a, b) => a.key - b.key); // אופציונלי: מיון לפי ID
        this.challengeMap = data.latestChallenges
        console.log(data)
        console.log(this.challengeMap)
      },
      error: (err) => {
        console.error("Failed to load home data", err);
        this.isLoading = false;
      }
    });
  }

  // --- פונקציות עזר ל-HTML ---

  // פונקציה להמרת ID לשם
  getCategoryName(id: number): string {
    return this.categoryNames[id]
  }


  // פונקציה להצגת תמונה (נניח שיש לך שירות תמונות)
  getImageUrl(base64Image: string): string {
    // אם התמונה היא Base64
    return `data:image/jpeg;base64,${base64Image}`;
  }

  // פונקציה לפורמט תאריך (דורש הזרקת DatePipe או שימוש ב-DatePipe ב-HTML)
  formatDate(date: string | Date): string {
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { day: 'numeric', month: 'short', year: 'numeric' });
  }

  // פונקציית מעקב ללולאת @for
  trackById(index: number, item: any): number {
    return item.id;
  }
}