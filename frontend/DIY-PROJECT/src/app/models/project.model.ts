import { UsersSimpleDTO } from "./user.model";

export interface ProjectListDTO {
  id: number;
  usersSimpleDTO: UsersSimpleDTO;
  title: string;
  picture: string;
  picturePath: string;
}
interface ProjectCreateDTO {
  title: string;
  description: string;
  materials: string;
  categoryId: number | null;
  challengeId?: number | null; 
  tagNames: string[]; 
  ages: string;
  timePrep: string;
  isDraft: boolean;
}
