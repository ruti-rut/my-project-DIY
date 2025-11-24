import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page, Project, ProjectListDTO } from '../models/project.model';

export type SortOption = 'newest' | 'oldest' | 'popular';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/project';
  private http = inject(HttpClient);

  uploadProject(formData: FormData): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/uploadProject`, formData);
  }

  getById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/getProject/${id}`);
  }

  getProjects(
    page: number,
    searchTerm: string = '',
    categoryIds: number[] = [],
    sort: 'newest' | 'oldest' | 'popular' = 'newest',
    size: number = 30
  ): Observable<Page<ProjectListDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (searchTerm.trim()) {
      params = params.set('search', searchTerm.trim());
    }

    // 🔥 שלח את כל הקטגוריות
    if (categoryIds.length > 0) {
      categoryIds.forEach(id => {
        params = params.append('categoryIds', id.toString());
      });
    }

    return this.http.get<Page<ProjectListDTO>>(`${this.apiUrl}/projects`, { params });
  }

getMyProjects(): Observable<ProjectListDTO[]> {
  return this.http.get<ProjectListDTO[]>('http://localhost:8080/api/project/myProjects');
}

assignProjectToChallenge(projectId: number, challengeId: number): Observable<any> {
  return this.http.patch(`${this.apiUrl}/${projectId}/assign-challenge/${challengeId}`, {});
}
}