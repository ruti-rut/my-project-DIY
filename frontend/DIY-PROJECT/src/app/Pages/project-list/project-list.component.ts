import { Component, DestroyRef, effect, inject, signal, OnInit } from '@angular/core';
import { fromEvent, debounceTime, distinctUntilChanged } from 'rxjs';
import { ProjectListDTO, Page } from '../../models/project.model';
import { ProjectService } from '../../services/project.service';
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
    MatCheckboxModule
  ],
  templateUrl: './project-list.html',
  styleUrl: './project-list.css'
})
export class ProjectListComponent implements OnInit {
  private projectService = inject(ProjectService);
  private categoryService = inject(CategoryService);
  private destroyRef = inject(DestroyRef);

  // Signals
  private _projects = signal<ProjectListDTO[]>([]);
  private _page = signal(0);
  private _loading = signal(false);
  private _hasMore = signal(true);
  private _searchTerm = signal('');
  private _selectedCategories = signal<number[]>([]);
  private _sortBy = signal<'newest' | 'oldest' | 'popular'>('newest');

  // Read-only signals
  projects = this._projects.asReadonly();
  loading = this._loading.asReadonly();
  hasMore = this._hasMore.asReadonly();
  cols = signal(3);

  // Categories
  categories = signal<any[]>([]);

  // Form Controls
  searchControl = new FormControl('');
  categoryControl = new FormControl<number[]>([]);
  sortControl = new FormControl<'newest' | 'oldest' | 'popular'>('newest');

  constructor() {
    this.setupResizeListener();
    this.setupSearchListener();
    this.setupCategoryListener();
    this.setupSortListener();

    effect(() => {
      const width = window.innerWidth;
      this.cols.set(width >= 1200 ? 3 : width >= 800 ? 2 : 1);
    });
  }

  ngOnInit() {
    this.loadCategories();
    this.loadMore();
  }

  private loadCategories() {
    this.categoryService.getAllCategories().subscribe({
      next: (cats) => this.categories.set(cats),
      error: () => alert('שגיאה בטעינת קטגוריות')
    });
  }

  loadMore() {
    if (this._loading() || !this._hasMore()) return;

    this._loading.set(true);
    
    this.projectService.getProjects(
      this._page(), 
      this._searchTerm(), 
      this._selectedCategories(),
      this._sortBy()
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

  clearSearch() {
    this.searchControl.setValue('');
  }

  clearCategories() {
    this.categoryControl.setValue([]);
  }

  // Toggle checkbox לקטגוריה
  onCategoryToggle(categoryId: number) {
    const current = this.categoryControl.value || [];
    if (current.includes(categoryId)) {
      this.categoryControl.setValue(current.filter(id => id !== categoryId));
    } else {
      this.categoryControl.setValue([...current, categoryId]);
    }
  }

  private setupSearchListener() {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((term: string | null) => {
        const cleanedTerm = (term || '').trim();
        if (this._searchTerm() !== cleanedTerm) {
          this._searchTerm.set(cleanedTerm);
          this.resetAndLoad();
        }
      });
  }

  private setupCategoryListener() {
    this.categoryControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((categories: number[] | null) => {
        const selectedCats = categories || [];
        if (JSON.stringify(this._selectedCategories()) !== JSON.stringify(selectedCats)) {
          this._selectedCategories.set(selectedCats);
          this.resetAndLoad();
        }
      });
  }

  private setupSortListener() {
    this.sortControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((sort) => {
        if (sort) {
          this._sortBy.set(sort);
          this.resetAndLoad();
        }
      });
  }

  private resetAndLoad() {
    this._projects.set([]);
    this._page.set(0);
    this._hasMore.set(true);
    this.loadMore();
  }

  private setupResizeListener() {
    fromEvent(window, 'resize')
      .pipe(
        debounceTime(200),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
  }

  trackById = (index: number, project: ProjectListDTO): number => project.id;
}