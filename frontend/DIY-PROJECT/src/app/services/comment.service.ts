import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { map, Observable } from "rxjs";
import { CommentCreateDTO, CommentDTO } from "../models/comment.model";
import { Page } from "../models/project.model";

@Injectable({ providedIn: 'root' })
export class CommentService {
  private apiUrl = 'http://localhost:8080/api/comment';
  private http = inject(HttpClient);

addComment(dto: CommentCreateDTO): Observable<CommentDTO> {
    return this.http.post<CommentDTO>(`${this.apiUrl}/addComment`, dto);
  }

  // ← תקן את זה!
getComments(projectId: number): Observable<CommentDTO[]> {
  return this.http.get<Page<CommentDTO>>(
    `${this.apiUrl}/project/${projectId}/comments`
  ).pipe(
    map(page => page.content) // ← רק הרשימה!
  );
}}