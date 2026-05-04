import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);

  if (!auth.authenticated()) {
    sessionStorage.setItem('redirectUrl', state.url);
    auth.login();   // ❗ kein await
    return false;
  }

  return auth.hasRole('admin');
};