export interface StepDTO {
  title: string;
  content: string;
  stepNumber: number;
  idProject: number;
}
export interface StepResponse {
  id: number;
  title: string;
  content: string;
  picturePath?: string;
  stepNumber: number;
}