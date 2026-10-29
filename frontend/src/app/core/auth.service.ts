import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, shareReplay, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, User } from './models';

const ACCESS_TOKEN_KEY = 'sk.accessToken';
const REFRESH_TOKEN_KEY = 'sk.refreshToken';

/**
 * Holds authentication state in signals so components can read it reactively
 * without subscribing manually.
 *
 * NOTE: tokens live in localStorage for simplicity. That is XSS-readable.
 * The hard alternative is an httpOnly, Secure, SameSite=Strict refresh cookie
 * plus a short-lived in-memory access token — see ADR-004 in docs/decisions.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly accessTokenSignal = signal<string | null>(localStorage.getItem(ACCESS_TOKEN_KEY));
  private readonly currentUserSignal = signal<User | null>(null);

  /** Shared in-flight refresh so ten parallel 401s trigger one refresh, not ten. */
  private refreshInFlight$: Observable<AuthResponse> | null = null;

  readonly accessToken = this.accessTokenSignal.asReadonly();
  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.accessTokenSignal() !== null);
  readonly isAdmin = computed(() => this.currentUserSignal()?.role === 'ADMIN');

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, { email, password })
      .pipe(tap((res) => this.applySession(res)));
  }

  register(email: string, password: string, fullName: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, { email, password, fullName })
      .pipe(tap((res) => this.applySession(res)));
  }

  /** Refreshes the token pair, de-duplicating concurrent calls. */
  refresh(): Observable<AuthResponse> {
    if (this.refreshInFlight$) {
      return this.refreshInFlight$;
    }
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    this.refreshInFlight$ = this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, { refreshToken })
      .pipe(
        tap((res) => this.applySession(res)),
        shareReplay({ bufferSize: 1, refCount: false }),
      ) as Observable<AuthResponse>;

    const inFlight = this.refreshInFlight$;
    inFlight.subscribe({ complete: () => (this.refreshInFlight$ = null), error: () => (this.refreshInFlight$ = null) });
    return inFlight;
  }

  /** Loads the signed-in user's profile from the API. */
  loadCurrentUser(): Observable<User> {
    return this.http
      .get<User>(`${environment.apiUrl}/me`)
      .pipe(tap((user) => this.currentUserSignal.set(user)));
  }

  logout(redirect = true): void {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (refreshToken) {
      // Fire and forget: the local session is cleared regardless of the API result.
      this.http.post(`${environment.apiUrl}/auth/logout`, { refreshToken }).subscribe({ error: () => {} });
    }
    this.clearSession();
    if (redirect) {
      void this.router.navigate(['/login']);
    }
  }

  private applySession(res: AuthResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, res.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    this.accessTokenSignal.set(res.accessToken);
    this.currentUserSignal.set(res.user);
  }

  private clearSession(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    this.accessTokenSignal.set(null);
    this.currentUserSignal.set(null);
  }
}
