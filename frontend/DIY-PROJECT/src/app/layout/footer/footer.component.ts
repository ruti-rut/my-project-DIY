import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    MatIconModule, 
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  currentYear = signal(new Date().getFullYear());
  
  // Dummy function for newsletter
  subscribe(email: string) {
    if(email) alert('Thanks for subscribing to the Creata newsletter!');
  }
}