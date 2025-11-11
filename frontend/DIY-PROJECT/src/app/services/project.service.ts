import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Project, ProjectListDTO } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/project';
 private http = inject(HttpClient);

  getAllProjects(): Observable<ProjectListDTO[]> {
    return this.http.get<ProjectListDTO[]>(`${this.apiUrl}/allProjects`);
  }
uploadProject(formData: FormData): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/uploadProject`, formData);
  }

  // קבלת פרויקט לפי ID
  getById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

}
