import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './Pages/header/header';

@Component({
  selector: 'app-root',
  imports: [HeaderComponent,RouterOutlet ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'DIY-PROJECT';
}
