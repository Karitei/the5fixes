import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../core/user.service';
import { NotificationService } from '../core/notification.service';
import { Role, User } from '../core/models';

@Component({
  selector: 'app-admin-users',
  imports: [ReactiveFormsModule],
  template: `
    <div class="space-y-6">
      <div class="flex items-end justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold tracking-tight">Users</h1>
          <p class="mt-1 text-slate-600">{{ total() }} account(s)</p>
        </div>
        <button type="button" class="btn-ghost" (click)="toggleCreate()">
          {{ showCreate() ? 'Cancel' : 'New user' }}
        </button>
      </div>

      @if (showCreate()) {
        <form class="card space-y-4" [formGroup]="createForm" (ngSubmit)="create()">
          <div class="grid gap-4 sm:grid-cols-2">
            <div>
              <label class="label" for="c-email">Email</label>
              <input id="c-email" class="field" formControlName="email" />
            </div>
            <div>
              <label class="label" for="c-name">Full name</label>
              <input id="c-name" class="field" formControlName="fullName" />
            </div>
            <div>
              <label class="label" for="c-password">Temporary password</label>
              <input id="c-password" type="password" class="field" formControlName="password" />
            </div>
            <div>
              <label class="label" for="c-role">Role</label>
              <select id="c-role" class="field" formControlName="role">
                @for (r of roles; track r) {
                  <option [value]="r">{{ r }}</option>
                }
              </select>
            </div>
          </div>
          <button type="submit" class="btn-primary" [disabled]="createForm.invalid">Create</button>
        </form>
      }

      <div class="card overflow-x-auto p-0">
        <table class="min-w-full divide-y divide-slate-200 text-sm">
          <thead class="bg-slate-50">
            <tr>
              <th class="px-4 py-3 text-left font-medium text-slate-600">Name</th>
              <th class="px-4 py-3 text-left font-medium text-slate-600">Email</th>
              <th class="px-4 py-3 text-left font-medium text-slate-600">Role</th>
              <th class="px-4 py-3 text-left font-medium text-slate-600">Status</th>
              <th class="px-4 py-3"></th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            @if (loading()) {
              <tr><td colspan="5" class="px-4 py-6 text-center text-slate-500">Loading…</td></tr>
            }
            @for (user of users(); track user.id) {
              <tr>
                <td class="px-4 py-3 font-medium">{{ user.fullName }}</td>
                <td class="px-4 py-3 text-slate-600">{{ user.email }}</td>
                <td class="px-4 py-3">
                  <span class="rounded-full bg-slate-100 px-2 py-0.5 text-xs">{{ user.role }}</span>
                </td>
                <td class="px-4 py-3">
                  <span [class]="user.enabled ? 'text-green-700' : 'text-slate-400'">
                    {{ user.enabled ? 'Active' : 'Disabled' }}
                  </span>
                </td>
                <td class="px-4 py-3 text-right">
                  <button type="button" class="text-red-600 hover:underline" (click)="remove(user)">
                    Delete
                  </button>
                </td>
              </tr>
            } @empty {
              @if (!loading()) {
                <tr><td colspan="5" class="px-4 py-6 text-center text-slate-500">No users found.</td></tr>
              }
            }
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-between text-sm">
        <button type="button" class="btn-ghost" [disabled]="page() === 0" (click)="go(page() - 1)">
          Previous
        </button>
        <span class="text-slate-600">Page {{ page() + 1 }} of {{ totalPages() }}</span>
        <button
          type="button"
          class="btn-ghost"
          [disabled]="page() >= totalPages() - 1"
          (click)="go(page() + 1)"
        >
          Next
        </button>
      </div>
    </div>
  `,
})
export class AdminUsersComponent {
  private readonly userService = inject(UserService);
  private readonly notifications = inject(NotificationService);
  private readonly fb = inject(FormBuilder);

  readonly roles: Role[] = ['USER', 'MANAGER', 'ADMIN'];
  readonly users = signal<User[]>([]);
  readonly loading = signal(false);
  readonly page = signal(0);
  readonly totalPages = signal(1);
  readonly total = signal(0);
  readonly showCreate = signal(false);

  readonly createForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    fullName: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['USER' as Role, Validators.required],
  });

  constructor() {
    this.load();
  }

  go(page: number): void {
    this.page.set(page);
    this.load();
  }

  toggleCreate(): void {
    this.showCreate.update((v) => !v);
  }

  load(): void {
    this.loading.set(true);
    this.userService.list('', this.page()).subscribe({
      next: (res) => {
        this.users.set(res.content);
        this.totalPages.set(Math.max(1, res.totalPages));
        this.total.set(res.totalElements);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.notifications.error(err.error?.message ?? 'Could not load users.');
        this.loading.set(false);
      },
    });
  }

  create(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    this.userService.create(this.createForm.getRawValue()).subscribe({
      next: () => {
        this.notifications.success('User created.');
        this.createForm.reset({ role: 'USER' });
        this.showCreate.set(false);
        this.load();
      },
      error: (err: HttpErrorResponse) =>
        this.notifications.error(err.error?.message ?? 'Could not create the user.'),
    });
  }

  remove(user: User): void {
    if (!confirm(`Delete ${user.fullName}? This cannot be undone.`)) {
      return;
    }
    this.userService.remove(user.id).subscribe({
      next: () => {
        this.notifications.success('User deleted.');
        this.load();
      },
      error: (err: HttpErrorResponse) =>
        this.notifications.error(err.error?.message ?? 'Could not delete the user.'),
    });
  }
}
