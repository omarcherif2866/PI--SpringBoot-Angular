import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { Reservation } from 'src/app/models/reservation';
import { ReservationService } from 'src/app/services/reservation.service';
import { CalendarOptions } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import { FullCalendarModule } from '@fullcalendar/angular'; // import FullCalendar

export interface Activite {
    id: number;
    name: string;
}

export interface Club {
    id: number;
    name: string;
}

export interface Evenement {
    id: number;
    name: string;
}

@Component({
    selector: 'app-reservation',
    templateUrl: './reservation.component.html',
    styleUrls: ['./reservation.component.scss'],
    providers: [MessageService]
})
export class ReservationComponent implements OnInit {

    calendarOptions: CalendarOptions;

    AddReservationDialog: boolean = false;
    UpdateReservationDialog: boolean = false;
    calendrierReservationDialog: boolean = false;
    deleteReservationDialog: boolean = false;
    deleteReservationsDialog: boolean = false;

    reservations: Reservation[] = [];
    reservation: Reservation = new Reservation();
    selectedReservations: Reservation[] = [];
    submitted: boolean = false;
    cols: any[] = [];
    showCalendar: boolean = false;
    activites: { id: number; nom: string; }[];
    clubs: { id: number; nom: string; }[];
    evenements: { id: number; nom: string; }[];

    constructor(private reservationService: ReservationService, private messageService: MessageService) { }

    ngOnInit() {
        this.getListReservations();

        this.activites = [
            { id: 1, nom: 'Activite 1' },
            { id: 2, nom: 'Activite 2' }
        ];
        console.log(this.activites);

        this.clubs = [
            { id: 1, nom: 'Club 1' },
            { id: 2, nom: 'Club 2' }
        ];
        console.log(this.clubs);

        this.evenements = [
            { id: 1, nom: 'Evenement 1' },
            { id: 2, nom: 'Evenement 2' }
        ];
        console.log(this.evenements);

        // Initialize calendarOptions
        this.calendarOptions = {
            plugins: [dayGridPlugin],
            initialView: 'dayGridMonth',
            events: []
        };
    }

    openNew() {
        this.reservation = new Reservation();
        this.submitted = false;
        this.AddReservationDialog = true;
    }

    deleteSelectedReservations() {
        this.deleteReservationsDialog = true;
    }

    editReservation(reservation: Reservation) {
        this.reservation = { ...reservation };
        this.reservation.dateDeb = new Date(this.reservation.dateDeb);
        this.reservation.dateFin = new Date(this.reservation.dateFin);
        this.UpdateReservationDialog = true;
    }

    deleteReservation(reservation: Reservation) {
        this.deleteReservationDialog = true;
        this.reservation = { ...reservation };
    }

    calendrierReservation() {
        this.calendrierReservationDialog = true;
        this.calendarOptions.events = this.reservations.map(reservation => ({
            title: reservation.lieu,
            start: reservation.dateDeb,
            end: reservation.dateFin
        }));
    }

    confirmDeleteSelected() {
        this.deleteReservationsDialog = false;
        this.selectedReservations.forEach(reservation => {
            this.reservationService.deleteReservation(reservation.id!).subscribe(() => {
                this.reservations = this.reservations.filter(val => val.id !== reservation.id);
            });
        });
        this.messageService.add({ severity: 'success', summary: 'Réussie', detail: 'Réservations supprimées', life: 3000 });
        this.selectedReservations = [];
        this.getListReservations();
    }

    confirmDelete() {
        this.deleteReservationDialog = false;
        this.reservationService.deleteReservation(this.reservation.id!).subscribe(() => {
            this.reservations = this.reservations.filter(val => val.id !== this.reservation.id);
            this.messageService.add({ severity: 'success', summary: 'Réussie', detail: 'Réservations supprimées', life: 3000 });
            this.reservation = new Reservation();
        });
    }

    hideDialog() {
        this.AddReservationDialog = false;
        this.UpdateReservationDialog = false;
        this.calendrierReservationDialog = false;
        this.submitted = false;
    }

    getListReservations() {
        this.reservationService.getAllReservations().subscribe(
            data => {
                this.reservations = data;
                console.log('Reservations data:', data);
            },
            error => {
                console.error('Error fetching reservations:', error);
            }
        );
    }

    convertToDate(localDate: any): Date {
        const dateParts = localDate.split('-');
        return new Date(+dateParts[0], +dateParts[1] - 1, +dateParts[2]);
    }

    saveReservation() {
        this.submitted = true;
        if (this.reservation.lieu && this.reservation.dateDeb && this.reservation.dateFin && this.reservation.lieu.trim()) {
            if (this.reservation.id) {
                this.reservationService.updateReservation(this.reservation.id, this.reservation).subscribe(() => {
                    this.reservations[this.findIndexById(this.reservation.id!)] = this.reservation;
                    this.messageService.add({ severity: 'success', summary: 'Réussie', detail: 'Réservation mise à jour', life: 3000 });
                    this.getListReservations();
                });
            } else {
                this.reservationService.createReservation(this.reservation).subscribe(newReservation => {
                    this.reservations.push(newReservation);
                    this.messageService.add({ severity: 'success', summary: 'Réussie', detail: 'Réservation créée', life: 3000 });
                    this.getListReservations();
                });
            }

            this.reservations = [...this.reservations];
            this.AddReservationDialog = false;
            this.UpdateReservationDialog = false;
            this.reservation = new Reservation();
        }
    }

    findIndexById(id: number): number {
        let index = -1;
        for (let i = 0; i < this.reservations.length; i++) {
            if (this.reservations[i].id === id) {
                index = i;
                break;
            }
        }
        return index;
    }

    onGlobalFilter(table: Table, event: Event) {
        table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
    }
}
