import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'audit' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/components/login.component').then((m) => m.LoginComponent),
    title: 'Sign in',
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/components/register.component').then((m) => m.RegisterComponent),
    title: 'Create account',
  },
  {
    path: 'audit',
    loadComponent: () => import('./features/audit/components/audit.component').then((m) => m.AuditComponent),
    title: 'Submit audit',
  },
   {
    path: 'dashboard',  
    loadComponent: () => import('./features/dashboard/components/dashboard.component').then((m) => m.DashboardComponent),
    title: 'Dashboard',
  }, 
  {
    path: 'admin/users',
    canActivate: [authGuard, roleGuard(['ADMIN'])],
    loadComponent: () =>
      import('./features/users/components/admin-users.component').then((m) => m.AdminUsersComponent),
    title: 'User administration',
  },
  { path: '**', redirectTo: 'audit' },
];
