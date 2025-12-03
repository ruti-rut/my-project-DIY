// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';


export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home-page/home-page').then(m => m.HomePageComponent)
  },
  {
    path: 'challenges',
    loadComponent: () => import('./features/challenge/challenge/challenge.component').then(m => m.ChallengesComponent)
  },
  {
    path: 'add-challenge',
    loadComponent: () => import('./features/challenge/add-challenge/add-challenge.component').then(m => m.AddChallengeComponent)
  },
  {
    path: 'sign-in',
    loadComponent: () => import('./features/auth/oauth2-success/sign-in/sign-in.component').then(m => m.SignInComponent)
  },
  {
    path: 'sign-up',
    loadComponent: () => import('./features/auth/oauth2-success/sign-up/sign-up.component').then(m => m.SignUpComponent)
  },
  {
    path: 'create-project',
    loadComponent: () => import('./features/project/project-create/project-create.component').then(m => m.ProjectCreateComponent),
    canActivate: [authGuard]
  },
  {
    path: 'projects-list',
    loadComponent: () => import('./features/project/project-list/project-list.component').then(m => m.ProjectListComponent)
  },
{
  path: 'oauth2/success',
  loadComponent: () => import('./features/auth/oauth2-success/oauth2-success.component').then(m => m.OAuth2SuccessComponent)
},
{
  path: 'projects/:id',
  loadComponent: () => import('./features/project/project-detail/project-detail/project-detail.component')
    .then(m => m.ProjectDetailComponent)
},
{
  path: 'diy-question',
  loadComponent: () => import('./features/chat/diy-chat/diy-chat/diy-chat.component')
    .then(m => m.DiyChatComponent),
  canActivate: [authGuard]
},
{
  path: 'challenge/:id',
loadComponent: () => import('./features/challenge/challenge-details/challenge-details/challenge-details.component')
    .then(m => m.ChallengeDetailsComponent), 
     title: 'Challenge Details'
},
{
    path: 'profile',
    loadComponent: () => import('./features/profile/profile-page/profile-page/profile-page.component').then(m => m.ProfilePageComponent),
    canActivate: [authGuard],
    title: 'My Profile'
  },
  {
    path: 'profile/edit',
    loadComponent: () => import('./features/profile/edit-profile/edit-profile/edit-profile.component').then(m => m.EditProfileComponent),
    canActivate: [authGuard],
    title: 'Edit Profile'
  },
  {
    path: 'project/edit/:id',
    loadComponent: () => import('./features/project/project-edit/project-edit/project-edit.component').then(m => m.ProjectEditComponent),
    canActivate: [authGuard],
    title: `Edit Project`
  },
  {
    path: 'my-projects',
    loadComponent: () => import('./features/profile/profile-page/profile-page/profile-page.component').then(m => m.ProfilePageComponent),
    canActivate: [authGuard],
    title: 'הפרויקטים שלי'
  },
    {
    path: 'oauth2/callback',
    loadComponent: () => import('./oauth2-callback/oauth2-callback.component').then(m => m.Oauth2CallbackComponent),
  },


  // נתיב Fallback (ללא שינוי)
  { path: '**', redirectTo: '' }
];