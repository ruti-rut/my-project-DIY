import { ProjectListDTO } from "./project.model";

export interface ChallengeListDTO {
  id: number;
  theme: string;
  endDate:string
  picture: string;
  picturePath: string;
}
export interface ChallengeResponseDTO {
  id: number;
  theme: string;
  content: string;
  startDate: string;
  endDate: string;
  picturePath?: string;
  picture?: string;
  status: 'OPEN' | 'UPCOMING' | 'CLOSED';
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
