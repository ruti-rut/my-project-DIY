import { CommentDTO,} from "./comment.model";
import { StepResponse } from "./step.model";
import { UsersSimpleDTO } from "./user.model";

export interface ProjectListDTO {
  id: number;
  title: string;
  picture: string;
  usersSimpleDTO: UsersSimpleDTO;
  // לא נשלח מהשרת – נשמור בלוקאל
  isFavorited?: boolean;
}
export interface ProjectCreateDTO {
  title: string;
  description: string;
  materials?: string;
  categoryId: number | null;
  challengeId?: number | null;
  ages?: string;
  timePrep?: string;
  tagNames: string[]; // רשימת שמות תגיות
  isDraft: boolean;
}
export interface Project {
  id: number;
  title: string;
  description: string;
  picture?: string;
  materials?: string;
  category?: { id: number; name: string };
  challenge?: { id: number } | null;
  tags?: { id: number; name: string }[];
  steps?: StepResponse[];
  isDraft: boolean;
  users?: UsersSimpleDTO;
  ages?: string;        // ← חדש
  timePrep?: string; 
  createdAt?: string;
  likesCount?: number;
  comments?: CommentDTO[]; // אם יש   // ← חדש
}
export interface Page<T> {
  content: T[];
  last: boolean;
  number: number;
  size: number;
}