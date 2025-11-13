// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

// אין צורך יותר לייבא את הקומפוננטות בראש הקובץ!
// Angular מטפל בזה באופן דינאמי בתוך loadComponent.

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home-page/home-page').then(m => m.HomePageComponent)
  },
  {
    path: 'add-project',
    loadComponent: () => import('./pages/add-project/add-project.component').then(m => m.AddProjectComponent)
  },
  {
    path: 'challenges',
    loadComponent: () => import('./pages/challenge/challenge.component').then(m => m.ChallengesComponent)
  },
  {
    path: 'add-challenge',
    loadComponent: () => import('./pages/add-challenge/add-challenge.component').then(m => m.AddChallengeComponent)
  },
  {
    path: 'sign-in',
    loadComponent: () => import('./pages/sign-in/sign-in.component').then(m => m.SignInComponent)
  },
  {
    path: 'sign-up',
    loadComponent: () => import('./pages/sign-up/sign-up.component').then(m => m.SignUpComponent)
  },
  {
    path: 'create-project',
    loadComponent: () => import('./project-create/project-create.component').then(m => m.ProjectCreateComponent),
    canActivate: [authGuard]
  },
  {
    path: 'projects-list',
    loadComponent: () => import('./pages/project-list/project-list.component').then(m => m.ProjectListComponent)
  },
{
  path: 'oauth2/success',
  loadComponent: () => import('./oauth2-success.component').then(m => m.OAuth2SuccessComponent)
},
{
  path: 'projects/:id',
  loadComponent: () => import('./project-detail/project-detail.component')
    .then(m => m.ProjectDetailComponent)
},

  // נתיב Fallback (ללא שינוי)
  { path: '**', redirectTo: '' }
];