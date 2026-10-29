import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';
import { NotificationService } from './core/notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen">
      @if (auth.isAuthenticated()) {
        <header class="border-b border-slate-200 bg-white">
          <div class="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
            <div class="flex items-center gap-6">
              <span class="font-semibold tracking-tight">Starter&nbsp;Kit</span>
              <nav class="flex items-center gap-4 text-sm">
                <a
                  routerLink="/dashboard"
                  routerLinkActive="text-brand-600 font-medium"
                  class="text-slate-600 hover:text-slate-900"
                  >Dashboard</a
                >
                @if (auth.isAdmin()) {
                  <a
                    routerLink="/admin/users"
                    routerLinkActive="text-brand-600 font-medium"
                    class="text-slate-600 hover:text-slate-900"
                    >Users</a
                  >
                }
              </nav>
            </div>

            <div class="flex items-center gap-3 text-sm">
              <span class="text-slate-600">{{ auth.currentUser()?.fullName }}</span>
              <span
                class="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-700"
                >{{ auth.currentUser()?.role }}</span
              >
              <button type="button" class="btn-ghost" (click)="auth.logout()">Sign out</button>
            </div>
          </div>
        </header>
      }

      <main class="mx-auto max-w-5xl px-4 py-8">
        <router-outlet />
      </main>

      <!-- Toasts -->
      <div class="pointer-events-none fixed bottom-4 right-4 z-50 flex w-80 flex-col gap-2">
        @for (toast of notifications.toasts(); track toast.id) {
          <div
            class="pointer-events-auto rounded-lg border px-4 py-3 text-sm shadow-lg"
            [class]="
              toast.kind === 'success'
                ? 'border-green-200 bg-green-50 text-green-900'
                : toast.kind === 'error'
                  ? 'border-red-200 bg-red-50 text-red-900'
                  : 'border-slate-200 bg-white text-slate-900'
            "
            role="status"
          >
            <div class="flex items-start justify-between gap-3">
              <span>{{ toast.message }}</span>
              <button
                type="button"
                class="text-xs opacity-60 hover:opacity-100"
                (click)="notifications.dismiss(toast.id)"
              >
                ✕
              </button>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class AppComponent {
  readonly auth = inject(AuthService);
  readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  constructor() {
    // Keep the shell honest if the session dies in another tab.
    window.addEventListener('storage', (event) => {
      if (event.key === 'sk.accessToken' && !event.newValue) {
        void this.router.navigate(['/login']);
      }
    });
  }
}
