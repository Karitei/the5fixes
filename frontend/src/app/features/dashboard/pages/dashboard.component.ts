import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';


@Component({
  selector: 'app-dashboard',
  imports: [DatePipe],
  templateUrl: 'dashboard.html',
})
export class DashboardComponent {
  readonly auth = inject(AuthService);
}
