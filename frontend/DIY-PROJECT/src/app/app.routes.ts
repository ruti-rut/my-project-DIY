// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { ChallengesComponent } from './pages/challenge/challenge.component';
import { AddProjectComponent } from './pages/add-project/add-project.component';
import { HomePageComponent } from './pages/home-page/home-page';

// ייבא את כל הקומפוננטות שלך מהתיקיות בתוך app/
// src/app/app.routes.ts
export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'add-project', component: AddProjectComponent },
  {path:'challenges',component:ChallengesComponent},
  { path: '**', redirectTo: '' }
];