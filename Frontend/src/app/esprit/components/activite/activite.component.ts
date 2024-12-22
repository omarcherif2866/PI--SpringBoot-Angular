import { Component } from '@angular/core';
import { Product } from '../../api/product';
import { ProductService } from '../../service/product.service';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { Activite, Annimateur } from '../../models/activite';
import { ActiviteService } from '../../service/activite.service';
import { SessionService } from '../../service/session.service';
import { Session } from '../../models/session';
import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Reservation } from 'src/app/models/reservation';
import { ReservationService } from 'src/app/services/reservation.service';
import { User } from '../../models/user';

@Component({
  providers: [MessageService],
  templateUrl: './activite.component.html',
  styles: [
    `
    .reservation-label {
        position: relative;
    }

    .reservation-details {
        display: none;
        position: absolute;
        background-color: white;
        border: 1px solid #ccc;
        padding: 10px;
        z-index: 10;
        width: 200px; /* Adjust the width as needed */
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .reservation-label:hover .reservation-details {
        display: block;
    }
    `
  ]
})
export class ActiviteComponent {
  activiteDialog: boolean = false;
  participantsDialog : boolean = false;
  SessionDialog: boolean = false;
  actionLabel: string = 'Enregistrer'; 

  deleteActiviteDialog: boolean = false;


  activites: Activite[] = [];

  sessions: Session[] = [];

  reservations: Reservation[] = [];


  activite: Activite = {};

  newSession: Session = {};

  selectedActivites: Activite[] = [];

  selectedReservations: any[] = [];

  submitted: boolean = false;

  annimateurOptions: any[] = [];
  selectedAnnimateur: Annimateur;
  selectedSessions: Session[] = [];
  // selectedReservations: Reservtion[] = [];
  participants: User[] = [];

  uploadedFiles: File[] = [];

  cols: any[] = [];

  statuses: any[] = [];

  rowsPerPageOptions = [5, 10, 20];

  constructor(private activiteService: ActiviteService, private messageService: MessageService, private sessionService: SessionService,
  private reservationService: ReservationService
  ) { }

  ngOnInit() {
    this.selectedSessions = []; 
    // this.selectedReservations = []; 

    this.getAllSessions();
    this.getAllReservations();

    this.getAnnimateurOptions();
    this.getAllActivites()

    this.cols = [
      { field: 'id', header: 'Code' },
      { field: 'nom', header: 'Nom' },
      { field: 'description', header: 'Description' },
      { field: 'annimateur', header: 'Annimateurs' },
      { field: 'sessions', header: 'Liste des Sessions' },
      { field: 'reservations', header: 'Liste des Reservations' },
      { field: 'nbrParticipants', header: 'Nombre de Places disponible' }
    ];
  
    
  }

  openNew() {
      this.activite = {};
      this.submitted = false;
      this.activiteDialog = true;
  }

  openNewSession() {
    this.newSession = {};
    this.submitted = false;
    this.SessionDialog = true;
  }

  editProduct(activite: Activite) {
    this.activite = { ...activite };
    this.activiteDialog = true;
    this.actionLabel = 'Modifier'; // Lors de l'édition, le libellé devient "Modifier"
}

deleteProduct(activite: Activite) {
  console.log('deleteProduct called with activite:', activite);
  if (activite && activite.id) { // Vérifiez que l'objet activite contient bien un ID
    this.deleteActiviteDialog = true;
    this.activite = { ...activite }; // Assurez-vous de copier l'objet activite correctement
  } else {
    console.error('Activite object is missing ID:', activite);
  }
}

confirmDelete() {
  console.log('confirmDelete called with activite:', this.activite);
  if (this.activite && this.activite.id) {
    this.activiteService.deleteActivite(this.activite.id).subscribe(
      response => {
        this.activites = this.activites.filter(val => val.id !== this.activite.id);
        this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Activity Deleted', life: 3000 });
        this.activite = {};
        this.deleteActiviteDialog = false;
      },
      error => {
        console.error('Error deleting activity:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to Delete Activity', life: 3000 });
        this.deleteActiviteDialog = false;
      }
    );
  } else {
    console.error('Invalid Activity ID:', this.activite);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Invalid Activity ID', life: 3000 });
    this.deleteActiviteDialog = false;
  }
}



  hideDialog() {
      this.activiteDialog = false;
      this.submitted = false;
  }


  

  saveActivite(): void {
    this.submitted = true;

    if (!this.activite.nom) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Nom est requis.' });
      return;
    }

    if (!this.activite.description) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Description est requise.' });
      return;
    }

    if (!this.activite.nbrParticipants) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Nombre de Participants est requis.' });
      return;
    }

    if (this.uploadedFiles.length === 0) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Image est requise.' });
      return;
    }

    if (!this.activite.annimateur) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Veuillez choisir un annimateur.' });
      return;
    }

    if (this.selectedSessions.length === 0) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Veuillez choisir au moins une session.' });
      return;
    }

    if (this.selectedReservations.length === 0) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Veuillez choisir au moins une réservation.' });
      return;
    }

    const formData = new FormData();
    formData.append('nom', this.activite.nom);
    formData.append('description', this.activite.description);
    formData.append('nbrParticipants', this.activite.nbrParticipants.toString()); // Convertir en chaîne de caractères
    formData.append('annimateur', this.activite.annimateur.toString());
    formData.append('image', this.uploadedFiles[0]);

    if (this.selectedSessions.length > 0) {
      const sessionIds = this.selectedSessions.map(session => session.id);
      formData.append('sessionIds', sessionIds.join(','));
    }

    if (this.selectedReservations.length > 0) {
      const reservationIds = this.selectedReservations.map(reservation => reservation.id);
      formData.append('reservationIds', reservationIds.join(','));
    }

    if (this.activite.id) {
      this.activiteService.putActivite(this.activite.id, formData).subscribe(
        res => {
          this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Activité mise à jour', life: 3000 });
          this.activiteDialog = false;
          window.location.reload();
        },
        (error: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de la mise à jour de l\'activité: ' + error.message });
        }
      );
    } else {
      this.activiteService.addActivite(formData).subscribe(
        res => {
          this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Activité ajoutée', life: 3000 });
          this.activiteDialog = false;
          this.activite = {};
          this.uploadedFiles = [];
        },
        (error: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de l\'ajout de l\'activité: ' + error.message });
        }
      );
    }
  }
  

  

  


  onGlobalFilter(table: Table, event: Event) {
      table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }

  getAllSessions(): void {
    this.sessionService.getSession().subscribe(ss => {
      this.sessions = ss;
      console.log("Sessions récupérées:", ss);
      // this.selectedSessions = ss; // Mettez à jour selectedSessions avec les sessions récupérées
    });
  }
  
  getAllReservations(): void {
    this.activiteService.getAllReservations().subscribe(rr => {
      this.reservations = rr;
      console.log("reservations récupérées:", rr);
      // this.selectedSessions = ss; // Mettez à jour selectedSessions avec les sessions récupérées
    });
  }

  getAnnimateurOptions(): void {
    this.annimateurOptions = Object.keys(Annimateur).map(key => ({
      label: Annimateur[key].replace(/_/g, ' '),  // Remplace les tirets bas par des espaces
      value: Annimateur[key]
    }));
  }

  onFileSelected(event): void {
    // Vérifier si la propriété 'files' est définie dans l'événement
    if (event.files && event.files.length > 0) {
        // Réinitialiser les fichiers sélectionnés précédemment
        this.uploadedFiles = [];

        // Ajouter les fichiers sélectionnés à uploadedFiles
        for (let file of event.files) {
            this.uploadedFiles.push(file);
        }
    } else {
        console.error("Erreur lors de la sélection du fichier : la propriété 'files' n'est pas définie dans l'événement.");
    }
}

  
  getImageUrl(imageName: string): string {
  // console.log("imageName:" , imageName)
  return `http://localhost:9090/activite/image/${imageName}`;
}

getAllActivites(): void {
  this.activiteService.getActivite().subscribe(
    ss => {
      this.activites = ss;
      console.log("Activités récupérées :", ss);
    },
    error => {
      console.error("Erreur lors de la récupération des activités :", error);
    }
  );
}


  saveSession() {
    this.submitted = true;
    if (!this.newSession.nom) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Nom est requis.' });
      return;
    }
    if (!this.newSession.activite) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Veuillez choisir une activité.' });
      return;
    }
    if (this.newSession.nom && this.newSession.activite) {
      const sessionData = {
        nom: this.newSession.nom,
        activite: this.newSession.activite
      };
      
      console.log('Session Data:', sessionData); // Vérifiez que les données sont correctement formatées
      
      const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
      this.sessionService.addSessionAndAssignToActivite(sessionData, this.newSession.activite.id)
        .subscribe({
          next: (data) => {
            this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Session ajoutée avec succès' });
            this.SessionDialog = false;
            this.getAllSessions(); // Rafraîchir la liste des sessions
          },
          error: (error) => {
            console.error('Erreur lors de l\'ajout de la session:', error);
            this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Échec de l\'ajout de la session' });
          }
        });
    }
  }
  
  
  

  closeDialog() {
    this.SessionDialog = false;
  }
  

  showParticipants(activite: Activite): void {
    this.activite = { ...activite }; // Copie de l'objet activite pour éviter les références partagées
    this.loadParticipantsByActivity(this.activite.id); // Chargement des participants de l'activité
    this.participantsDialog = true; // Affichage du dialogue
  }
  loadParticipantsByActivity(id: number): void {
    this.activiteService.getParticipantsByActivity(id).subscribe(
      participants => {
        this.participants = participants;
      },
      error => {
        console.log('Erreur lors du chargement des participants : ', error);
      }
    );
  }

  hideParticipantsDialog(): void {
    this.participantsDialog = false; // Pour cacher le dialogue des participants
  }
  
  
}
