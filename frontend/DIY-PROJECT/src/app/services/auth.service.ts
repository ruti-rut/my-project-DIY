import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap, catchError, Observable, of } from 'rxjs';
import { Router } from '@angular/router';
import { UserResponseDTO, UsersRegisterDTO, UserLogInDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // כתובת ה-URL הבסיסית של שרת ה-Spring Boot שלך
  private baseUrl = 'http://localhost:8080/api/auth'; // *** שנה אם צריך ***

  private currentUserSignal = signal<UserResponseDTO | null>(null);
  public currentUser = this.currentUserSignal.asReadonly();

  private http = inject(HttpClient);
  private router = inject(Router);
  constructor() {
        // מפעיל את בדיקת האימות מיד כאשר השירות מוזרק
        this.checkAuthentication().subscribe(); 
    }

  checkAuthentication(): Observable<UserResponseDTO | null> {
    return this.http.get<UserResponseDTO>(`${this.baseUrl}/me`).pipe(
      tap(user => {
        this.currentUserSignal.set(user);
      }),
      catchError(() => {
        this.currentUserSignal.set(null);
        return of(null);
      })
    );
  }

  /**
   * פונקציה לרישום משתמש חדש
   */
signUp(userData: UsersRegisterDTO): Observable<UserResponseDTO> {
  return this.http.post<UserResponseDTO>(`${this.baseUrl}/signup`, userData).pipe(
    tap(user => {
      this.currentUserSignal.set(user);
    })
  );
}
  /**
   * פונקציה להתחברות משתמש
   */
signIn(credentials: UserLogInDTO): Observable<UserResponseDTO> {
  return this.http.post<UserResponseDTO>(`${this.baseUrl}/signin`, credentials).pipe(
    tap(response => {
      this.currentUserSignal.set(response);
      this.router.navigate(['/']);
    })
  );
}
  signInWithGoogle(): void {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }


  logout(): Observable<any> {
  return this.http.post(`${this.baseUrl}/logout`, {}, { withCredentials: true }).pipe(
    tap(() => {
      this.currentUserSignal.set(null);
      this.router.navigate(['/']);
    }),
    catchError(err => {
      this.currentUserSignal.set(null);
      this.router.navigate(['/']);
      return of(err);
    })
  );
}
}