// src/app/core/auth/admin.guard.ts
import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = async (_route, state) => {
  const auth = inject(AuthService);

  if (!auth.authenticated()) {
    await auth.login(window.location.origin + state.url);
    return false;
  }

  if (!auth.hasRole('admin')) {
    return false;
  }

  return true;
};