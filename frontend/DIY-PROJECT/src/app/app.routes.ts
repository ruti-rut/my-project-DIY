// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page';
import { AddProjectComponent } from './add-project/add-project.component';

// ייבא את כל הקומפוננטות שלך מהתיקיות בתוך app/
// src/app/app.routes.ts
export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'add-project', component: AddProjectComponent },
  { path: '**', redirectTo: '' }
];