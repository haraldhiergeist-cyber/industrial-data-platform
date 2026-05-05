import { Injectable, computed, signal } from '@angular/core';
import Keycloak, { KeycloakProfile } from 'keycloak-js';
import { keycloakConfig } from './keycloak.config';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = new Keycloak(keycloakConfig);
  private readonly loginAttemptStorageKey = 'loginRedirectAttempt';
  private readonly maxLoginAttempts = 2;
  private readonly loginAttemptWindowMs = 60_000;
  private readonly keycloakCallbackParams = new Set([
    'code',
    'state',
    'session_state',
    'iss'
  ]);

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
      this.resetLoginAttempts();
      this.profile.set(await this.keycloak.loadUserProfile());
    }
  }

  async login(): Promise<void> {
    const redirectUrl = sessionStorage.getItem('redirectUrl') || '/';

    await this.keycloak.login({
      redirectUri: this.toAbsoluteRedirectUri(redirectUrl)
    });
  }

  rememberRedirectUrl(url: string): void {
    sessionStorage.setItem('redirectUrl', this.withoutKeycloakCallbackParams(url));
  }

  async loginTo(url: string): Promise<boolean> {
    this.rememberRedirectUrl(url);

    if (!this.registerLoginAttempt(url)) {
      return false;
    }

    await this.login();
    return true;
  }

  async logout(): Promise<void> {
    await this.keycloak.logout({
      redirectUri: this.toAbsoluteRedirectUri('/')
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

  private toAbsoluteRedirectUri(redirectUrl: string): string {
    return new URL(
      this.withoutKeycloakCallbackParams(redirectUrl),
      document.baseURI
    ).toString();
  }

  private withoutKeycloakCallbackParams(url: string): string {
    const redirectUrl = new URL(url, document.baseURI);

    for (const param of this.keycloakCallbackParams) {
      redirectUrl.searchParams.delete(param);
    }

    return `${redirectUrl.pathname}${redirectUrl.search}${redirectUrl.hash}`;
  }

  private registerLoginAttempt(url: string): boolean {
    const redirectUrl = this.withoutKeycloakCallbackParams(url);
    const now = Date.now();
    const previous = this.readLoginAttempt();
    const attempt =
      previous &&
      previous.redirectUrl === redirectUrl &&
      now - previous.startedAt < this.loginAttemptWindowMs
        ? previous
        : { redirectUrl, count: 0, startedAt: now };

    if (attempt.count >= this.maxLoginAttempts) {
      return false;
    }

    sessionStorage.setItem(
      this.loginAttemptStorageKey,
      JSON.stringify({
        ...attempt,
        count: attempt.count + 1
      })
    );

    return true;
  }

  private readLoginAttempt(): {
    redirectUrl: string;
    count: number;
    startedAt: number;
  } | null {
    const rawAttempt = sessionStorage.getItem(this.loginAttemptStorageKey);

    if (!rawAttempt) {
      return null;
    }

    try {
      return JSON.parse(rawAttempt);
    } catch {
      return null;
    }
  }

  private resetLoginAttempts(): void {
    sessionStorage.removeItem(this.loginAttemptStorageKey);
  }
}
