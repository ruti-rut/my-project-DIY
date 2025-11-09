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

