import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject, Inject } from "@angular/core";
import { catchError, throwError } from "rxjs";
import { NotificationService } from "../services/notification.service";


export const errorInterceptor : HttpInterceptorFn = (req,next) =>{
    
  const notifications = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      console.log('INTERCEPTOR:', error.status);
 
      if (error.status === 500) {
        notifications.error('Server error. Please try again later.');
      }

      return throwError(() => error);
    })
  );
}