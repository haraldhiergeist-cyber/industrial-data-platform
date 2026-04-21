import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async (_route, state) => {
  const auth = inject(AuthService);

  if (auth.authenticated()) {
    return true;
  }

  await auth.login(window.location.origin + state.url);
  return false;
};