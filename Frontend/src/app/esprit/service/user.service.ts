import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { StorageService } from './storage.service';
import { User } from '../models/user';
import { map } from 'rxjs/operators';

const AUTH_API = 'http://localhost:9090/api/v1/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {


  blockUser(userId: number): Observable<any> {
    return this.http.put(AUTH_API +'block/'+ userId , null, { responseType: 'text' });
  }
  

  unblockUser(userId: number): Observable<any> {
    return this.http.put(AUTH_API +'unblock/'+ userId, {}, { responseType: 'text' }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any): Observable<any> {
    console.error('Error:', error);
    return throwError(error); // Rethrow the error for handling in the component
  }

  updateRole(userId: number, newRole : string): Observable<User> {
    const body = { role: newRole};
    const accessToken = sessionStorage.getItem('refreshTokenKey');

    // Prepare headers with Authorization token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${accessToken}`
    });
    return this.http.put<User>(AUTH_API + 'role/'+ userId, body, { headers }).pipe(
      catchError(error => {
        return throwError(error);
      })
    );
  }
  
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(AUTH_API +'users');
  }

  constructor(private http: HttpClient, private storageService: StorageService) {}

  login(username: string, password: string,twofactorcode: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'authenticate',
      {
        username,
        password,
        twofactorcode,
      },
      httpOptions
    );
  }
  
  register(username: string, email: string, tel: string,password: string,  image: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'register',
      {
        username,
        email,
        tel,
        password,
        image,
      },
      httpOptions
    );
  }

  
  getUserProfile(): Observable<any> {
    const accessToken = sessionStorage.getItem('refreshTokenKey');

    // Prepare headers with Authorization token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${accessToken}`
    });
    return this.http.get(AUTH_API +'profile',{ headers }).pipe(
      catchError(error => {
        return throwError(error);
      })
    );
  }

  updateUserProfile(userId: number, user: any): Observable<any> {
    const accessToken = sessionStorage.getItem('refreshTokenKey');

    // Prepare headers with Authorization token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${accessToken}`
    });
    return this.http.put(AUTH_API + userId, user, { headers }).pipe(
      catchError(error => {
        return throwError(error);
      })
    );
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'forgot-password/' + email,
      {},
      { responseType: 'text' }
    );
  }
  // Handle logout
  logout(): void {

    document.cookie = 'teymour=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; Secure';

    this.storageService.clean();
  }

  verifyVerificationCode(username: string, verificationCode: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'verify-verification-code',
      {
        username,
        verificationCode
      },
      httpOptions
    );
  }

  deleteUser(userId: number): Observable<any> {
    return this.http.delete(AUTH_API + 'users/' + userId);
  }

  modifyUser(userId: number, user: any): Observable<any> {
    return this.http.put(AUTH_API + 'users/' + userId, user, httpOptions);
  }

  activate2FA(userId: number): Observable<any> {
    return this.http.post(
      AUTH_API + `activate-2fa/${userId}`,
      {},
      httpOptions
    );
  }

  modifyUserProfile(userId: number, userProfile: any): Observable<any> {
    return this.http.put(
      AUTH_API + 'profile/' + userId,
      userProfile,
      httpOptions
    );
  }

  activateExposant(userId: number): Observable<any> {
    return this.http.post(
      AUTH_API + `activateExposant/${userId}`,
      {},
      httpOptions
    );
  }

  registerWithGoogle(): Observable<any> {
    return this.http.post(AUTH_API + 'register-with-google', {}, {});
  }

  verifyPhoneNumber(userId: number, code: string): Observable<any> {
    return this.http.post(
      AUTH_API + 'verify-code',
      {
        userId,
        code
      },
      httpOptions
    );
  }
}
