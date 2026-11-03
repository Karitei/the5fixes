import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl : 'register.html' ,
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
