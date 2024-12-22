import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';
import { Session } from '../models/session';
import { environment } from 'src/environments/environment';

const API_URL = environment.endPoint+"/session";


@Injectable({
  providedIn: 'root'
})
export class SessionService {

  constructor(private http: HttpClient, private router: Router) { }

  getSessionById(id: any): Observable<Session> {
    return this.http.get<Session>(`${API_URL}/${id}`);
  } 

  getSession() {
    return this.http.get<Session[]>(API_URL + "/allSessions");
  }



  addSession(data: any): Observable<Session> {
    return this.http.post<Session>(API_URL, data)
      .pipe(
        catchError((error: any) => {
          console.error('Erreur lors de l\'ajout de la session:', error);
          return throwError('Une erreur s\'est produite lors de l\'ajout de la session. Veuillez réessayer.');
        })
      );
  }

  putSession(id: any, session: any): Observable<Session> {
    return this.http.put<Session>(`${API_URL}/${id}`, session)
    .pipe(
      catchError((error: any) => {
        console.error('Erreur lors de l\'ajout de session:', error);
        return throwError('Une erreur s\'est produite lors de l\'ajout de session. Veuillez réessayer.');
      })
    );
  }
  
  


  deleteSession(id:any):Observable<Session>{
    return this.http.delete<Session>(`${API_URL}/${id}`)

  }

  addSessionAndAssignToActivite(session: Session, activiteId: number): Observable<Session> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<Session>(`${API_URL}/addSessionAndAssignToActivite/${activiteId}`, session, { headers });
        
  }

  getCountAllSessions(): Observable<number> {
    return this.http.get<number>(`${API_URL}/countSessions`);
  }

}
