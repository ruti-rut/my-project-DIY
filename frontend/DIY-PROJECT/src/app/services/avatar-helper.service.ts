import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AvatarHelperService {

  // רשימת צבעים לבחירה (צבעים מנוגדים ונעימים)
  private colors = [
    '#2196F3', // כחול
    '#4CAF50', // ירוק
    '#FF9800', // כתום
    '#9C27B0', // סגול
    '#00BCD4', // תכלת
    '#E91E63'  // ורוד
  ];

  /**
   * מחזירה את האות הראשונה בשם המשתמש (גדולה).
   * @param name שם המשתמש
   */
  getFirstInitial(name: string | null | undefined): string {
    if (!name) {
      return '?';
    }
    return name.charAt(0).toUpperCase();
  }

  /**
   * יוצר Hash קבוע מהשם כדי לבחור צבע קבוע.
   * @param name שם המשתמש
   */
  generateColor(name: string | null | undefined): string {
    if (!name) {
      return this.colors[0]; // ברירת מחדל
    }
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      // יצירת Hash פשוט
      hash = name.charCodeAt(i) + ((hash << 5) - hash); 
    }
    // הבטחת אינדקס בתוך גבולות רשימת הצבעים
    const index = Math.abs(hash) % this.colors.length;
    return this.colors[index];
  }
}