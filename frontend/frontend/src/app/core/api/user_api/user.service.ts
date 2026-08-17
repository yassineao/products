import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environment';
import { LoginResponse, UserRequest, UserResponse } from '../../interfaces/User';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http: HttpClient = inject(HttpClient);
  private apiUrl: string = environment.apiUrl;

  login(user: UserRequest): void {
    this.http
      .post<LoginResponse>(`${this.apiUrl}/user/login`, user, {
        withCredentials: true,
      })
      .subscribe(({ accessToken, id, name, email, role }) => {
        const authenticatedUser: UserResponse = { id, name, email, role };

        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('user', JSON.stringify(authenticatedUser));
      });
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
  }

  me(): Observable<boolean> {
    return this.http
      .get<UserResponse>(`${this.apiUrl}/user/me`, {
        withCredentials: true,
      })
      .pipe(
        map(() => true),
        catchError(() => of(false)),
      );
  }

  refresh(): void {
    this.http.post<LoginResponse>(`${this.apiUrl}/user/refresh`, {
      withCredentials: true,
    })
      .subscribe(({ accessToken, id, name, email, role }) => {
        const authenticatedUser: UserResponse = { id, name, email, role };

        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('user', JSON.stringify(authenticatedUser));
      });
  }

  health(): void {
    this.http.get(`${this.apiUrl}/health`, {})
  }
}
