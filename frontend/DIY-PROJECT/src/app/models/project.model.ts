import { UsersSimpleDTO } from "./user.model";

export interface ProjectListDTO {
  id: number;
  usersSimpleDTO: UsersSimpleDTO;
  title: string;
  picture: string;
  picturePath: string;
}