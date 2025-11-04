
export interface ChallengeListDTO {
  id: number;
  theme: string;
  endDate:string
  picture: string;
  picturePath: string;
}
export interface ChallengeCreateDTO {
  startDate: string; // נשתמש ב-string בטופס עבור תאריכים
  endDate: string;
  theme: string;
  content: string; // תוכן/תיאור האתגר
}

// מודל הישות המלאה שהשרת מחזיר
export interface Challenge {
  id: number;
  startDate: string;
  endDate: string;
  theme: string;
  content: string;
  picturePath: string;
}
