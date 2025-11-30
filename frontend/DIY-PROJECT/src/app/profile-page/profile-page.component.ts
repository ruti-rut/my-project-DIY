import { Component, inject } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { CommonModule } from '@angular/common';
import { MyProjectsTabComponent } from '../my-projects-tab/my-projects-tab.component';
import { ProfileHeaderComponent } from '../profile-header/profile-header.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FavoritesTabComponent } from '../favorites-tab/favorites-tab.component';
import { MatTabsModule } from '@angular/material/tabs';
@Component({
  selector: 'app-profile-page',
  imports: [CommonModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    ProfileHeaderComponent,
    MyProjectsTabComponent,
    FavoritesTabComponent],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent {
  service = inject(ProfileService);

  ngOnInit(): void {
    this.service.loadProfile();
  }


}
