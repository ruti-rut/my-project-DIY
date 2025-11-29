import { ProjectListDTO } from "./project.model";

export interface UsersSimpleDTO {
  id: number;
  userName: string;
  profilePicture: string;           // base64 string
  profilePicturePath: string;       // path to the image (if stored on server)
}
export enum AuthProvider {
  LOCAL = 'LOCAL',
  GOOGLE = 'GOOGLE'
}

export interface UserResponseDTO {
  id: number;
  userName: string;
  mail: string;
  city: string;
  aboutMe: string;
  profilePicturePath: string | null;
  provider: AuthProvider;
  subscribedToDaily:boolean;

}

export interface UsersRegisterDTO {
  userName: string;
  password: string;
  mail: string;
}

// DTO עבור התחברות (תואם ל-UserLogInDTO ב-Spring)
export interface UserLogInDTO {
  identifier: string; // זהו ה-userName או email
  password: string;
}
export interface UserProfileDTO {
  id: number;
  userName: string;
  mail: string;
  city: string;
  aboutMe: string;
  profilePicturePath?: string;
  joinDate: string;
  myProjects: ProjectListDTO[];
  favoriteProjects: ProjectListDTO[];
  projectsCount: number;
  favoritesCount: number;
}