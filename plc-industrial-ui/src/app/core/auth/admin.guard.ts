import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = async (_route, state) => {
  const auth = inject(AuthService);

  if (!auth.authenticated()) {
    auth.rememberRedirectUrl(state.url);
    await auth.login();
    return false;
  }

  return auth.hasRole('admin');
};
