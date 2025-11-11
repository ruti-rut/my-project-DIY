import { CommentResponse } from "./comment.model";
import { StepResponse } from "./step.model";
import { UsersSimpleDTO } from "./user.model";

export interface ProjectListDTO {
  id: number;
  usersSimpleDTO: UsersSimpleDTO;
  title: string;
  picture: string;
  picturePath: string;
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
  picturePath?: string;
  materials?: string;
  category?: { id: number; name: string };
  challenge?: { id: number } | null;
  tags?: { id: number; name: string }[];
  steps?: StepResponse[];
  isDraft: boolean;
  users?: { id: number };
  ages?: string;        // ← חדש
  timePrep?: string; 
  createdAt?: string;
  likesCount?: number;
  comments?: CommentResponse[]; // אם יש   // ← חדש
}