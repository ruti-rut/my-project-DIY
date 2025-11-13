import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page, Project, ProjectListDTO } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/project';
  private http = inject(HttpClient);

  uploadProject(formData: FormData): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/uploadProject`, formData);
  }

  // קבלת פרויקט לפי ID
  getById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/getProject/${id}`);
  }

  getProjects(page: number, size: number = 30): Observable<Page<ProjectListDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<ProjectListDTO>>(`${this.apiUrl}/allProjects`, { params });
  }



}
