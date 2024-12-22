import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, map, throwError } from 'rxjs';
import { Activite } from '../models/activite';
import { Reservation } from 'src/app/models/reservation';
import { environment } from 'src/environments/environment';
import { User } from '../models/user';

const API_URL = environment.endPoint+"/activite";


@Injectable({
  providedIn: 'root'
})
export class ActiviteService {

  constructor(private http: HttpClient, private router: Router) { }

  getActiviteById(id: any): Observable<Activite> {
    return this.http.get<Activite>(`${API_URL}/${id}`);
  } 

  getActivite() {
    return this.http.get<Activite[]>(API_URL + "/allActivites");
  }



  addActivite(data: any): Observable<Activite> {
    return this.http.post<Activite>(API_URL, data)
    .pipe(
      catchError((error: any) => {
        console.error('Erreur lors de l\'ajout de l\'activite:', error);
        return throwError('Une erreur s\'est produite lors de l\'ajout de l\'activite. Veuillez réessayer.');
      })
    );
}

  putActivite(id: number, formData: any): Observable<Activite> {
  return this.http.put<Activite | HttpErrorResponse>(`${API_URL}/${id}`, formData)
    .pipe(
      map((response: any) => {
        // Vérifier si la réponse est une instance de HttpErrorResponse
        if (response instanceof HttpErrorResponse) {
          // Si c'est une erreur HTTP, propager l'erreur
          throw response;
        } else {
          // Sinon, retourner la réponse comme une instance d'Activite
          return response as Activite;
        }
      }),
      catchError((error: HttpErrorResponse) => {
        // Traiter les erreurs HTTP ici
        console.error('Erreur lors de la mise à jour de l\'activité:', error);
        // Retourner une erreur observable
        return throwError('Une erreur s\'est produite lors de la mise à jour de l\'activité. Veuillez réessayer.');
      })
    );
}


  deleteActivite(id:any):Observable<Activite>{
    console.log('deleteActivite called with id:', id);
    return this.http.delete<Activite>(`${API_URL}/${id}`)

  }

  participateInActivity(activityId: number, participantId: number): Observable<any> {
    const url = `${API_URL}/${participantId}/participate/${activityId}`;
    return this.http.post(url, {}).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An error occurred';
        if (error.error instanceof ErrorEvent) {
          // Client-side error
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // Server-side error
          errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        }
        console.error(errorMessage);
        return throwError(errorMessage);
      })
    );
  }

  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>("http://localhost:9090/api/v1/reservations");
  }

  getActivityParticipationCount(): Observable<Object[]> {
    const url = `${API_URL}/participation-count`;
    return this.http.get<Object[]>(url);
  }

  getMostPopularActivity(): Observable<any[]> {
    return this.http.get<any[]>(`${API_URL}/most-popular`);
}

getActivitySessionsCount(): Observable<any[]> {
  return this.http.get<any[]>(`${API_URL}/activity-sessions-count`);
}

getCountAllActivites(): Observable<number> {
  return this.http.get<number>(`${API_URL}/countActivity`);
}

getTotalBudgetPerActivity(): Observable<any[]> {
  return this.http.get<any[]>(`${API_URL}/total-budget-per-activity`);
}

getParticipantsByActivity(id:any): Observable<User[]> {
  return this.http.get<User[]>(`${API_URL}/${id}/participants`);
}
}

  

