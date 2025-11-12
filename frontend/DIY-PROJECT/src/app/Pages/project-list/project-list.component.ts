import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { fromEvent, debounceTime } from 'rxjs';
import { ProjectListDTO, Page } from '../../models/project.model';
import { ProjectService } from '../../services/project.service';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProjectCardComponent } from '../../project-card/project-card.component';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';  
import { MatGridListModule } from '@angular/material/grid-list';

@Component({
  selector: 'app-project-list',
  imports: [MatGridListModule,
    ProjectCardComponent,
    MatButtonModule,
    MatProgressSpinnerModule,
    CommonModule,],
  templateUrl: './project-list.html',
  styleUrl: './project-list.css'
})
export class ProjectListComponent {
private projectService = inject(ProjectService);
  private destroyRef = inject(DestroyRef);

  private _projects = signal<ProjectListDTO[]>([]);
  private _page = signal(0);
  private _loading = signal(false);
  private _hasMore = signal(true);

  projects = this._projects.asReadonly();
  loading = this._loading.asReadonly();
  hasMore = this._hasMore.asReadonly();
  cols = signal(3);

  constructor() {
    this.loadMore();
    this.setupResizeListener();

    effect(() => {
      const width = window.innerWidth;
      this.cols.set(width >= 1200 ? 3 : width >= 800 ? 2 : 1);
    });
  }

  loadMore() {
    if (this.loading() || !this.hasMore()) return;

    this._loading.set(true);
    this.projectService.getProjects(this._page()).subscribe({
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
