// src/app/services/toast.service.ts
import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class ToastService {
  private snackBar = inject(MatSnackBar);

  success(message: string) {
    this.snackBar.open(message, 'סבבה', {
      duration: 3000,
      panelClass: ['toast-success'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  error(message: string) {
    this.snackBar.open(message, 'אוקיי', {
      duration: 5000,
      panelClass: ['toast-error'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  info(message: string) {
    this.snackBar.open(message, '', {
      duration: 2500,
      panelClass: ['toast-info']
    });
  }
}