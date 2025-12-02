import { ChallengeListDTO } from "./challenge.model";
import { ProjectListDTO } from "./project.model";

export interface HomeResponseDTO {
  projectsPerCategory: Record<number, ProjectListDTO[]>; 
  latestChallenges: ChallengeListDTO[];
}