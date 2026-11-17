import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable, switchMap, takeWhile, timer } from "rxjs";
import { AuditResponse, AuditStatusResponse } from "../models/audit.model";
import { environment } from "../../../../environments/environment";


@Injectable({providedIn:'root'})
export class AuditService{
    private readonly http = inject(HttpClient);

    private get<T>(path: string): Observable<T> {
      return this.http.get<T>(`${environment.apiUrl}${path}`);
    }

    private post<T, B>(path: string, body: B): Observable<T> {
    return this.http.post<T>(
        `${environment.apiUrl}${path}`, body);
    }

    pollAudit(jobId: string): Observable<AuditStatusResponse> {
    return timer(0, 2500).pipe(
        switchMap(() => this.get<AuditStatusResponse>(`/audit/${jobId}`)),
        takeWhile(r => r.status === 'PENDING' || r.status === 'RUNNING', true),
    );
    }
    

    submitAudit(url: string) : Observable<AuditResponse>{ 
       return this.post<AuditResponse, { url: string }>('/audit/submitAudit', { url });
        
    }
}