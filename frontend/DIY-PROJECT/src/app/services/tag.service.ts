import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class TagService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/tag'; // בהנחה שיש לך endpoint כזה

  getAllTags(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/allTags`);
  }
}