import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async (_route, state) => {
  const auth = inject(AuthService);

  if (auth.authenticated()) {
    return true;
  }

  sessionStorage.setItem('redirectUrl', state.url);
  await auth.login();
  return false;
};
