import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe],
  template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-semibold tracking-tight">
          Welcome, {{ auth.currentUser()?.fullName }}
        </h1>
        <p class="mt-1 text-slate-600">
          This is your starter dashboard. Replace it with real product content.
        </p>
      </div>

      <div class="grid gap-4 sm:grid-cols-3">
        <div class="card">
          <p class="text-sm text-slate-500">Signed in as</p>
          <p class="mt-1 font-medium break-all">{{ auth.currentUser()?.email }}</p>
        </div>
        <div class="card">
          <p class="text-sm text-slate-500">Role</p>
          <p class="mt-1 font-medium">{{ auth.currentUser()?.role }}</p>
        </div>
        <div class="card">
          <p class="text-sm text-slate-500">Member since</p>
          <p class="mt-1 font-medium">
            {{ auth.currentUser()?.createdAt | date: 'mediumDate' }}
          </p>
        </div>
      </div>

      <div class="card">
        <h2 class="font-semibold">What's already wired up</h2>
        <ul class="mt-3 space-y-2 text-sm text-slate-700">
          <li>• JWT authentication with refresh-token rotation and revocation</li>
          <li>• Role-based access control (USER / MANAGER / ADMIN)</li>
          <li>• Optimistic locking and full audit columns on every entity</li>
          <li>• Business audit trail written automatically on login success and failure</li>
          <li>• Typed API error handling with per-field validation messages</li>
          <li>• OpenAPI documentation at <code class="rounded bg-slate-100 px-1">/swagger-ui.html</code></li>
        </ul>
      </div>
    </div>
  `,
})
export class DashboardComponent {
  readonly auth = inject(AuthService);
}
