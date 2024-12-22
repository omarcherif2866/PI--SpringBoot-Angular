import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Reservation } from '../models/reservation';
import { Observable } from 'rxjs';


const API_URL = environment.endPoint+"/api/v1/reservations";

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

    constructor(private _http: HttpClient) { }
    getAllReservations(): Observable<Reservation[]> {
        return this._http.get<Reservation[]>(API_URL);
      }

      getReservationById(id: number): Observable<Reservation> {
        return this._http.get<Reservation>(`${API_URL}/${id}`);
      }

      createReservation(reservation: Reservation): Observable<Reservation> {
        return this._http.post<Reservation>(API_URL, reservation);
      }

      updateReservation(id: number, reservation: Reservation): Observable<Reservation> {
        return this._http.put<Reservation>(`${API_URL}/${id}`, reservation);
      }

      deleteReservation(id: number): Observable<void> {
        return this._http.delete<void>(`${API_URL}/${id}`);
      }
    }
