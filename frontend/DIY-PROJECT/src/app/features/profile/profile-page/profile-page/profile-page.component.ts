import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { ProfileHeaderComponent } from '../../profile-header/profile-header/profile-header.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProfileService } from '../../../../services/profile.service';
import { FavoritesTabComponent } from '../../../../shared/components/favorites-tab/favorites-tab/favorites-tab.component';
import { MyProjectsTabComponent } from '../../../../shared/components/my-projects-tab/my-projects-tab/my-projects-tab.component';
import { DraftsTabComponent } from "../../../../shared/components/drafts-tab/drafts-tab.component";
import { MatIcon } from "@angular/material/icon";
@Component({
  selector: 'app-profile-page',
  imports: [CommonModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    ProfileHeaderComponent,
    MyProjectsTabComponent,
    FavoritesTabComponent, DraftsTabComponent, MatIcon],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent {
  service = inject(ProfileService);

  ngOnInit(): void {
    this.service.loadProfile();
  }


}
