export interface UsersSimpleDTO {
  id: number;
  name: string;
  email?: string; // אם יש שדות אופציונליים
  // הוסף כאן את השדות שקיימים בצד השרת
}