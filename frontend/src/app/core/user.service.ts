import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PageResponse, Role, User } from './models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  list(query: string, page = 0, size = 20): Observable<PageResponse<User>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (query.trim()) {
      params = params.set('q', query.trim());
    }
    return this.http.get<PageResponse<User>>(`${environment.apiUrl}/admin/users`, { params });
  }

  create(payload: { email: string; password: string; fullName: string; role: Role }): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/admin/users`, payload);
  }

  update(id: string, payload: { fullName: string; role: Role; enabled: boolean }): Observable<User> {
    return this.http.put<User>(`${environment.apiUrl}/admin/users/${id}`, payload);
  }

  remove(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/users/${id}`);
  }
}
