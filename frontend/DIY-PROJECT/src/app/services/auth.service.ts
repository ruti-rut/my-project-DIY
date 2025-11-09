import { Injectable } from "@angular/core";
import { AuthResponse, UserResponseDTO } from "../models/user.model";
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from "@angular/router";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
private apiUrl = 'http://localhost:8080/api/autho';
private userSubject = new BehaviorSubject<UserResponseDTO | null>(null);
  public user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.checkIfLoggedIn(); // בודק אם יש עוגייה
  }
  // הרשמה
  signup(data: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/signup`, data, {
      withCredentials: true  // שולח עוגיות
    }).pipe(
      tap(res => this.saveUser(res.user))
    );
  }

  // התחברות
  login(data: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, data, {
      withCredentials: true
    }).pipe(
      tap(res => this.saveUser(res.user))
    );

  }
// התחברות עם Google
  loginWithGoogle() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }

  // התנתקות
  logout() {
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe();
    this.clearUser();
    this.router.navigate(['/login']);
  }

  // בדיקה אם מחובר
  isLoggedIn(): boolean {
    return this.userSubject.value !== null;
  }

  getUser(): UserResponseDTO | null {
    return this.userSubject.value;
  }

  // שומר את המשתמש
  private saveUser(user: UserResponseDTO) {
    this.userSubject.next(user);
  }

  // מנקה
  private clearUser() {
    this.userSubject.next(null);
  }
  
  private checkIfLoggedIn() {
  this.http.get<AuthResponse>(`${this.apiUrl}/me`, { withCredentials: true })
    .subscribe({
      next: (res) => this.saveUser(res.user),  // יש JWT → שומר את המשתמש
      error: () => this.clearUser()            // אין JWT → מנקה
    });
}
}
