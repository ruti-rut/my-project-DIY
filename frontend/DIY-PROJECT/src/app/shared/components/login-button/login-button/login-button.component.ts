import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {  MatIconModule } from "@angular/material/icon";
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-login-button',
  imports: [RouterLink, RouterLinkActive, MatButtonModule, MatIconModule],
  templateUrl: './login-button.component.html',
  styleUrl: './login-button.component.css'
})
export class LoginButtonComponent {

}
