import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Project, ProjectListDTO } from '../models/project.model';
import { StepResponse } from '../models/step.model';

@Injectable({
  providedIn: 'root'
})
export class StepService {
  private apiUrl = 'http://localhost:8080/api/step';
 private http = inject(HttpClient);


uploadStep(formData: FormData): Observable<StepResponse> {
    return this.http.post<StepResponse>(`${this.apiUrl}/uploadStep`, formData);
  }

  editStep(stepId: number, stepData: FormData): Observable<StepResponse> {
    // שימוש ב-PUT עם ה-ID בנתיב
    return this.http.put<StepResponse>(`${this.apiUrl}/editStepWithImage/${stepId}`, stepData);
  }

  deleteStep(stepId: number): Observable<void> {
    // שימוש ב-DELETE עם ה-ID בנתיב
    return this.http.delete<void>(`${this.apiUrl}/deleteStep/${stepId}`);
  }



}
