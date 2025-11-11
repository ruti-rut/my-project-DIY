import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProjectListDTO } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/projects';
 private http = inject(HttpClient);

  getAllProjects(): Observable<ProjectListDTO[]> {
    return this.http.get<ProjectListDTO[]>(`${this.apiUrl}/allProjects`);
  }


}
