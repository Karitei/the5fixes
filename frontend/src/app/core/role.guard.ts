import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from './auth.service';
import { Role } from './models';

/**
 * Client-side role guard. This is UX only — the API enforces authorisation
 * independently. Never rely on a frontend guard for security.
 */
export const roleGuard = (allowed: Role[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    const check = (role?: Role) => (role ? allowed.includes(role) : false);

    if (auth.currentUser()) {
      return check(auth.currentUser()!.role) ? true : router.createUrlTree(['/dashboard']);
    }
    return auth.loadCurrentUser().pipe(
      map((user) => (check(user.role) ? true : router.createUrlTree(['/dashboard']))),
      catchError(() => of(router.createUrlTree(['/login']))),
    );
  };
};
