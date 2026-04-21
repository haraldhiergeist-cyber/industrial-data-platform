import { Injectable, computed, signal } from '@angular/core';
import Keycloak, { KeycloakProfile } from 'keycloak-js';
import { keycloakConfig } from './keycloak.config';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = new Keycloak(keycloakConfig);

  readonly authenticated = signal(false);
  readonly profile = signal<KeycloakProfile | null>(null);

  readonly username = computed(() =>
    this.profile()?.firstName ||
    this.profile()?.username ||
    this.profile()?.email ||
    'User'
  );

  async init(): Promise<void> {
    const loggedIn = await this.keycloak.init({
      onLoad: 'check-sso',
      pkceMethod: 'S256',
      responseMode: 'query',
      checkLoginIframe: false
    });

    this.authenticated.set(loggedIn);

    if (loggedIn) {
      this.profile.set(await this.keycloak.loadUserProfile());
    }
  }

  async login(redirectUri?: string): Promise<void> {
    await this.keycloak.login({
      redirectUri: redirectUri ?? window.location.origin + '/',
      prompt: 'login'
    });
  }

  async logout(): Promise<void> {
    await this.keycloak.logout({
      redirectUri: window.location.origin + '/'
    });
  }

  async getToken(): Promise<string | undefined> {
    if (!this.keycloak.authenticated) {
      return undefined;
    }

    await this.keycloak.updateToken(30);
    return this.keycloak.token;
  }

  hasRole(role: string): boolean {
    return this.keycloak.hasRealmRole(role);
  }
}