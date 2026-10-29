import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Attaches the bearer token to every API request, and transparently retries once
 * after refreshing an expired access token.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  const isAuthCall = req.url.includes('/auth/');
  const token = auth.accessToken();

  const request =
    !isAuthCall && token
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const canRefresh = error.status === 401 && !isAuthCall;

      if (!canRefresh) {
        return throwError(() => error);
      }

      return auth.refresh().pipe(
        switchMap((res) =>
          next(req.clone({ setHeaders: { Authorization: `Bearer ${res.accessToken}` } })),
        ),
        catchError((refreshError) => {
          // The refresh token is dead too: the session is over.
          auth.logout();
          return throwError(() => refreshError);
        }),
      );
    }),
  );
};
