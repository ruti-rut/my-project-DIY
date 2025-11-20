// src/app/guards/auth.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { firstValueFrom } from 'rxjs';

export const authGuard: CanActivateFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // מחכה שהבדיקה תסתיים (גם אם המשתמש כבר מחובר)
  const user = await firstValueFrom(authService.checkAuthentication());

  if (!user) {
    router.navigate(['/sign-in']);
    return false;
  }
  return true;
};