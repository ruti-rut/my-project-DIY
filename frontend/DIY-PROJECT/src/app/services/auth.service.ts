import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap, catchError, Observable, of } from 'rxjs';
import { Router } from '@angular/router';
import { UserResponseDTO, UsersRegisterDTO, UserLogInDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
private baseUrl = 'http://localhost:8080/api/auth';

  private currentUserSignal = signal<UserResponseDTO | null>(null);
  public currentUser = this.currentUserSignal.asReadonly();

  private http = inject(HttpClient);
  private router = inject(Router);

  constructor() {
    this.checkAuthentication().subscribe();
  }

  // פונקציה ציבורית לעדכון המשתמש – משמשת מכל מקום באפליקציה
  updateCurrentUser(user: UserResponseDTO | null) {
      console.log('AuthService: updateCurrentUser', user); // לוג לבדיקה
    this.currentUserSignal.set(user);
  }

  checkAuthentication(): Observable<UserResponseDTO | null> {
    return this.http.get<UserResponseDTO>(`${this.baseUrl}/me`).pipe(
      tap(user => this.updateCurrentUser(user)),
      catchError(() => {
        this.updateCurrentUser(null);
        return of(null);
      })
    );
  }

  signUp(userData: UsersRegisterDTO): Observable<UserResponseDTO> {
    return this.http.post<UserResponseDTO>(`${this.baseUrl}/signup`, userData).pipe(
      tap(user => this.updateCurrentUser(user))
    );
  }

  signIn(credentials: UserLogInDTO): Observable<UserResponseDTO> {
    return this.http.post<UserResponseDTO>(`${this.baseUrl}/signin`, credentials).pipe(
      tap(response => {
        this.updateCurrentUser(response);
        this.router.navigate(['/']);
      })
    );
  }

  signInWithGoogle(): void {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }

  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/logout определить`, {}, { withCredentials: true }).pipe(
      tap(() => {
        this.updateCurrentUser(null);
        this.router.navigate(['/']);
      }),
      catchError(err => {
        this.updateCurrentUser(null);
        this.router.navigate(['/']);
        return of(err);
      })
    );
  }
}