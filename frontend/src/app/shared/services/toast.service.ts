import { Injectable, signal } from "@angular/core";


export interface ToastMessage{
    message : string;
    type : 'success' | 'error' | 'warning';
}

@Injectable({
    providedIn:'root'
})

export class ToastService{
    private readonly _toast = signal<ToastMessage | null>(null);

    readonly toast = this._toast.asReadonly();

    show(message:string , type: ToastMessage['type']='error') : void{
         this._toast.set({ message, type });
    }

    clear(): void {
    this._toast.set(null);
  }
}

