import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {

  // קבל את ה-Token מ-localStorage
  const token = localStorage.getItem('jwt_token');

  // שכפל את הבקשה עם withCredentials
  let clonedReq = req.clone({
    withCredentials: true
  });

  // אם יש Token, הוסף אותו ל-Authorization Header
  if (token) {
    clonedReq = clonedReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    });
  }

  return next(clonedReq);
};