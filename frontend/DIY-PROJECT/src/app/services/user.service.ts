import { inject, Injectable, signal } from "@angular/core";
import { Observable, tap } from "rxjs";
import { UserResponseDTO } from "../models/user.model";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {
private usersUrl = 'http://localhost:8080/api/users';
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  // משתמשים ב-currentUser של AuthService – הכי נכון!
  readonly currentUser = this.authService.currentUser;

toggleDailySubscription(): Observable<UserResponseDTO> {
  return this.http.put<UserResponseDTO>(`${this.usersUrl}/subscription/toggle`, {}).pipe(
    tap(updatedUser => {
      // ← זה מה שגורם ל-computed להפעיל שוב!
      this.authService.updateCurrentUser({ ...updatedUser });
      // או אפשר גם: this.authService.updateCurrentUser(structuredClone(updatedUser));
    })
  );
}
}




