import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HomeResponseDTO } from '../models/home-page.model'; // ייבוא המודל החדש

@Injectable({
  providedIn: 'root'
})
export class HomePageService {
  // הנחה: ה-Controller שלך מופה ל- /api/home
  private baseUrl = 'http://localhost:8080/api/home'; 

  constructor(private http: HttpClient) {}

  /**
   * שולף את כל נתוני דף הבית (פרויקטים לפי קטגוריה ואתגרים).
   */
  getHomeData(): Observable<HomeResponseDTO> {
    return this.http.get<HomeResponseDTO>(`${this.baseUrl}/all`);
  }
}