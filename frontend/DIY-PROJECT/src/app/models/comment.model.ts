import { UsersSimpleDTO } from "./user.model";

export interface CommentCreateDTO {
  content: string;
  projectId: number;
}

export interface CommentDTO {
  id: number;
  content: string;
  createdAt: string; // ISO date
  user: UsersSimpleDTO;
}