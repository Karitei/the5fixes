import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth/auth.service';
import { NotificationService } from './core/services/notification.service';
import { ToastComponent } from './shared/components/toast.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ToastComponent],
  templateUrl : 'app.html',
})
export class AppComponent {
  readonly auth = inject(AuthService);
  readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  constructor() {   
    window.addEventListener('storage', (event) => {
      if (event.key === 'sk.accessToken' && !event.newValue) {
        void this.router.navigate(['/login']);
      }
    });
  }
}
