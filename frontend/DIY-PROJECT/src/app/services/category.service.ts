import { HttpClient } from "@angular/common/http";
import { Category } from "../models/category.model";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/category';

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/allCategories`);
  }
  getCategoryById(id:number): Observable<Category>{
    return this.http.get<Category>(`${this.apiUrl}/category`)
  }
}