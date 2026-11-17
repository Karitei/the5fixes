import { Component, inject} from "@angular/core";
import { NotificationService } from "../../core/services/notification.service";



@Component({
  selector: 'app-toast', 
  standalone: true,
  templateUrl: 'toast.html'
})

export class ToastComponent{
  protected readonly notificationService = inject(NotificationService);
}