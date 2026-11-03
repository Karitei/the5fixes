import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { NotificationService } from './core/services/notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl : 'app.html',
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
