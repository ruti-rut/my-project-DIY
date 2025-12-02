import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LikeService {
private apiUrl = 'http://localhost:8080/api/project';

    constructor(private http: HttpClient) { }

    addToLiked(projectId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${projectId}/like`, {});
    }

    removeFromLiked(projectId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${projectId}/like`);
    }

}
