// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { AddChallengeComponent } from './Pages/add-challenge/add-challenge.component';
import { AddProjectComponent } from './Pages/add-project/add-project.component';
import { ChallengesComponent } from './Pages/challenge/challenge.component';
import { HomePageComponent } from './Pages/home-page/home-page';

// ייבא את כל הקומפוננטות שלך מהתיקיות בתוך app/
// src/app/app.routes.ts
export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'add-project', component: AddProjectComponent },
  { path: 'challenges', component: ChallengesComponent },
  { path: 'add-challenge', component: AddChallengeComponent },
  { path: '**', redirectTo: '' }
];