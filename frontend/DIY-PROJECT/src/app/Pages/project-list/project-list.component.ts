import { Component, DestroyRef, effect, inject, signal, OnInit } from '@angular/core';
import { fromEvent, debounceTime, distinctUntilChanged } from 'rxjs';
import { ProjectListDTO, Page } from '../../models/project.model';
import { ProjectService, SortOption } from '../../services/project.service';
import { CategoryService } from '../../services/category.service';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProjectCardComponent } from '../../project-card/project-card.component';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';  
import { MatGridListModule } from '@angular/material/grid-list';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FilterParams, ProjectFiltersComponent } from "../../project-filters/project-filters.component";

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [
    MatGridListModule,
    ProjectCardComponent,
    MatButtonModule,
    MatProgressSpinnerModule,
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule,
    MatCheckboxModule,
    ProjectFiltersComponent
],
  templateUrl: './project-list.html',
  styleUrl: './project-list.css'
})
export class ProjectListComponent implements OnInit {
private projectService = inject(ProjectService);
  
  // Signals
  private _projects = signal<ProjectListDTO[]>([]);
  private _page = signal(0);
  private _loading = signal(false);
  private _hasMore = signal(true);

  // נתונים שהתקבלו מקומפוננטת הפילטרים
  private _currentSearchTerm = signal('');
  private _currentSelectedCategories = signal<number[]>([]);
  private _currentSortBy = signal<SortOption>('newest');

  // Read-only signals
  projects = this._projects.asReadonly();
  loading = this._loading.asReadonly();
  hasMore = this._hasMore.asReadonly();
  cols = signal(3);

  constructor() {
    // השארנו רק את ה-effect של שינוי רוחב המסך
    effect(() => {
      const width = window.innerWidth;
      this.cols.set(width >= 1200 ? 3 : width >= 800 ? 2 : 1);
    });
  }

  ngOnInit() {
    // טעינה ראשונית - כעת הפילטרים נשלחים דרך onFilterChange בפעם הראשונה
    this.loadMore();
  }

  // **מטודה חדשה:** מקבלת את כל פרמטרי הסינון מקומפוננטת הבת
  onFilterChange(params: FilterParams) {
    this._currentSearchTerm.set(params.searchTerm);
    this._currentSelectedCategories.set(params.categoryIds);
    this._currentSortBy.set(params.sort as SortOption); // ודא ש-SortOption מיובא

    this.resetAndLoad();
  }

  // המטודה שנטענת כאשר לוחצים "טען עוד" (היא ממשיכה את העמוד הבא)
  loadMore() {
    if (this._loading() || !this._hasMore()) return;

    this._loading.set(true);
    
    this.projectService.getProjects(
      this._page(), 
      this._currentSearchTerm(), 
      this._currentSelectedCategories(),
      this._currentSortBy()
    ).subscribe({
      next: (page: Page<ProjectListDTO>) => {
        this._projects.update(projects => [...projects, ...page.content]);
        this._page.update(p => p + 1);
        this._hasMore.set(!page.last);
        this._loading.set(false);
      },
      error: () => {
        this._loading.set(false);
        alert('שגיאה בטעינת פרויקטים');
      }
    });
  }

  // מאפס את הרשימה, חוזר לעמוד 0 וקורא ל-loadMore
  private resetAndLoad() {
    this._projects.set([]);
    this._page.set(0);
    this._hasMore.set(true);
    // loadMore יופעל אוטומטית כי _loading הפך ל-false
    this.loadMore();
  }

  trackById = (index: number, project: ProjectListDTO): number => project.id;
}