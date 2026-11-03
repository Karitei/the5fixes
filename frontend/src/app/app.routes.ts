import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/pages/login/login.component').then((m) => m.LoginComponent),
    title: 'Sign in',
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/pages/register/register.component').then((m) => m.RegisterComponent),
    title: 'Create account',
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/pages/dashboard.component').then((m) => m.DashboardComponent),
    title: 'Dashboard',
  },
  {
    path: 'admin/users',
    canActivate: [authGuard, roleGuard(['ADMIN'])],
    loadComponent: () =>
      import('./features/users/admin/admin-users.component').then((m) => m.AdminUsersComponent),
    title: 'User administration',
  },
  { path: '**', redirectTo: 'dashboard' },
];
