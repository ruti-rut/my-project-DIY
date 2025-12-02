import { CommonModule } from '@angular/common';
import { Component, DestroyRef, EventEmitter, inject, Output, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { map, debounceTime, distinctUntilChanged } from 'rxjs';
import { MatChip, MatChipSet, MatChipOption } from "@angular/material/chips";
import { CategoryService } from '../../../../services/category.service';
export interface FilterParams {
  searchTerm: string;
  categoryIds: number[];
  sort: 'newest' | 'oldest' | 'popular';
}
@Component({
  selector: 'app-project-filters',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule,
    MatCheckboxModule,
    MatButtonModule, MatChipSet, MatChipOption],
  templateUrl: './project-filters.component.html',
  styleUrl: './project-filters.component.css'
})
export class ProjectFiltersComponent {
private categoryService = inject(CategoryService);
  private destroyRef = inject(DestroyRef);
  
  // פלט: משדר החוצה את השינויים
  @Output() filterChange = new EventEmitter<FilterParams>();

  // Categories
  categories = signal<any[]>([]);

  // Form Controls
  searchControl = new FormControl('');
  categoryControl = new FormControl<number[]>([]);
  sortControl = new FormControl<'newest' | 'oldest' | 'popular'>('newest');

  ngOnInit() {
    this.loadCategories();
    this.setupListeners();
  }

  private loadCategories() {
    this.categoryService.getAllCategories().subscribe({
      next: (cats) => this.categories.set(cats),
      error: () => alert('שגיאה בטעינת קטגוריות')
    });
  }

  // מאגד את כל המאזינים למקום אחד
  private setupListeners() {
    // אנו מאזינים לכל שינוי בכל אחד מהפקדים
    this.searchControl.valueChanges
      .pipe(
        map(term => (term || '').trim()), 
        debounceTime(300),
        distinctUntilChanged(), 
        takeUntilDestroyed(this.destroyRef)
      ).subscribe(() => this.emitFilterChange());

    this.categoryControl.valueChanges
      .pipe(
        // השתמשנו ב-JSON.stringify בעבר, עכשיו distinctUntilChanged מספיק ל-number[] 
        // במקרה ש-Angular מטפל בהשוואה של מערכים (בדרך כלל זה עובד לערכים פרימיטיביים)
        distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)),
        takeUntilDestroyed(this.destroyRef)
      ).subscribe(() => this.emitFilterChange());

    this.sortControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.emitFilterChange());

    // שידור ראשוני של הפרמטרים (כדי שהרשימה תטען פעם ראשונה)
    this.emitFilterChange(); 
  }

  // **מטודה מרכזית:** משדרת את כל הפרמטרים הנוכחיים החוצה
  private emitFilterChange() {
    const params: FilterParams = {
      searchTerm: this.searchControl.value || '',
      categoryIds: this.categoryControl.value || [],
      sort: this.sortControl.value || 'newest',
    };
    this.filterChange.emit(params);
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
}
