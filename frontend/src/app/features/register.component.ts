import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { NotificationService } from '../core/notification.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="mx-auto mt-16 max-w-md">
      <div class="card">
        <h1 class="text-xl font-semibold">Create your account</h1>

        <form class="mt-6 space-y-4" [formGroup]="form" (ngSubmit)="submit()" novalidate>
          <div>
            <label class="label" for="fullName">Full name</label>
            <input id="fullName" class="field" formControlName="fullName" autocomplete="name" />
          </div>

          <div>
            <label class="label" for="email">Email</label>
            <input id="email" type="email" class="field" formControlName="email" autocomplete="email" />
          </div>

          <div>
            <label class="label" for="password">Password</label>
            <input
              id="password"
              type="password"
              class="field"
              formControlName="password"
              autocomplete="new-password"
            />
            <p class="mt-1 text-xs text-slate-500">At least 8 characters.</p>
          </div>

          @if (fieldErrors().length) {
            <ul class="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">
              @for (msg of fieldErrors(); track msg) {
                <li>{{ msg }}</li>
              }
            </ul>
          }

          <button type="submit" class="btn-primary w-full" [disabled]="loading()">
            {{ loading() ? 'Creating…' : 'Create account' }}
          </button>
        </form>

        <p class="mt-4 text-center text-sm text-slate-600">
          Already registered?
          <a routerLink="/login" class="font-medium text-brand-600 hover:underline">Sign in</a>
        </p>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  readonly loading = signal(false);
  readonly fieldErrors = signal<string[]>([]);

  readonly form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.fieldErrors.set(['Please correct the highlighted fields.']);
      return;
    }
    this.loading.set(true);
    this.fieldErrors.set([]);

    const { fullName, email, password } = this.form.getRawValue();
    this.auth.register(email, password, fullName).subscribe({
      next: () => {
        this.notifications.success('Account created.');
        void this.router.navigate(['/dashboard']);
      },
      error: (err: HttpErrorResponse) => {
        // Maps the API's fieldErrors object back to readable messages.
        const fields = err.error?.fieldErrors as Record<string, string> | undefined;
        this.fieldErrors.set(
          fields ? Object.entries(fields).map(([k, v]) => `${k}: ${v}`) : [err.error?.message ?? 'Registration failed.'],
        );
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }
}
