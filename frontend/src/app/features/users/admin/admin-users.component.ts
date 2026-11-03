import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Role, User } from '../../../core/models/models';

@Component({
  selector: 'app-admin-users',
  imports: [ReactiveFormsModule],
  templateUrl : 'admin.html',
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
