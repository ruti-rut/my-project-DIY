export interface StepDTO {
  title: string;
  content: string;
  stepNumber: number;
  projectId: number;
  picturePath: string;
}
export interface StepResponse {
  id: number;
  title: string;
  content: string;
  picture?: string;
  stepNumber: number;
  picturePath?: string;
}