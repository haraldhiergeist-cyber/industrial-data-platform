import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { from } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const apiBasePath = environment.apiBasePath;
  const isRelativeApiCall = req.url.startsWith('/api');
  const isConfiguredApiCall = apiBasePath ? req.url.startsWith(apiBasePath) : false;

  if (!isRelativeApiCall && !isConfiguredApiCall) {
    return next(req);
  }

  return from(authService.getToken()).pipe(
    switchMap(token => {
      if (!token) {
        return next(req);
      }

      return next(
        req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        })
      );
    })
  );
};