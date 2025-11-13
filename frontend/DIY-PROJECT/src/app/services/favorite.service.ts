import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class FavoriteService {
    private apiUrl = 'http://localhost:8080/api/project';

    constructor(private http: HttpClient) { }

    addToFavorites(projectId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${projectId}/favorite`, {});
    }

    removeFromFavorites(projectId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${projectId}/favorite`);
    }

}