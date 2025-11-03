import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProjectListDTO } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/projects';

  constructor(private _httpClient: HttpClient) { }

  getAllProjects(): Observable<ProjectListDTO[]> {
    return this._httpClient.get<ProjectListDTO[]>(`${this.apiUrl}/allProjects`);
  }
}
