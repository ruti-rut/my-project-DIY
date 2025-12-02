import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-oauth2-callback',
  imports: [],
  templateUrl: './oauth2-callback.component.html',
  styleUrl: './oauth2-callback.component.css'
})
export class Oauth2CallbackComponent implements OnInit {
    private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const error = params['error'];

      // אם יש שגיאה
      if (error) {
        console.error('❌ OAuth2 error:', error);
        this.router.navigate(['/sign-in'], { 
          queryParams: { error: 'המשתמש לא נמצא במערכת. יש להירשם תחילה.' } 
        });
        return;
      }

      // אם יש Token
      if (token) {
        console.log('✅ Token received, saving and loading user...');
        
        // שמור את ה-Token ב-localStorage
        localStorage.setItem('jwt_token', token);
        
        // טען את פרטי המשתמש מהשרת
        this.authService.loadCurrentUser().subscribe({
          next: (user) => {
            console.log('✅ User loaded successfully:', user);
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            console.error('❌ Failed to load user:', err);
            localStorage.removeItem('jwt_token');
            this.router.navigate(['/sign-in'], {
              queryParams: { error: 'שגיאה בטעינת פרטי המשתמש' }
            });
          }
        });
      } else {
        // אין Token ואין שגיאה - משהו לא תקין
        console.error('❌ No token or error in callback');
        this.router.navigate(['/sign-in']);
      }
    });
  }

}
