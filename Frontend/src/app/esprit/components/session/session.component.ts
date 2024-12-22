import { Component, OnInit } from '@angular/core';
import { Product } from 'src/app/esprit/api/product';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ProductService } from 'src/app/esprit/service/product.service';
import { Session } from '../../models/session';
import { SessionService } from '../../service/session.service';

@Component({
    providers: [MessageService],
    templateUrl: './session.component.html',
})
export class SessionComponent {

  sessionDialog: boolean = false;
  actionLabel: string = 'Enregistrer'; 

  deleteSessionDialog: boolean = false;

  deleteSessionsDialog: boolean = false;

  sessions: Session[] = [];

  ses: Session = {};

  selectedProducts: Product[] = [];

  submitted: boolean = false;

  cols: any[] = [];

  statuses: any[] = [];

  rowsPerPageOptions = [5, 10, 20];

  constructor(private messageService: MessageService, private sessionService: SessionService,
  ) { }

  ngOnInit() {
      this.getAllSession();

      this.cols = [
          { field: 'product', header: 'Product' },
          { field: 'price', header: 'Price' },
          { field: 'category', header: 'Category' },
          { field: 'rating', header: 'Reviews' },
          { field: 'inventoryStatus', header: 'Status' }
      ];

      this.statuses = [
          { label: 'INSTOCK', value: 'instock' },
          { label: 'LOWSTOCK', value: 'lowstock' },
          { label: 'OUTOFSTOCK', value: 'outofstock' }
      ];
  }

  openNew() {
      this.ses = {};
      this.submitted = false;
      this.sessionDialog = true;
  }

  deleteSelectedProducts() {
      this.deleteSessionsDialog = true;
  }

  editProduct(session: Session) {
    this.ses = { ...session };
    this.sessionDialog = true;
    this.actionLabel = 'Modifier'; // Lors de l'édition, le libellé devient "Modifier"

}

  deleteProduct(session: Session) {
      this.deleteSessionDialog = true;
      this.ses = { ...this.ses };
  }

  // confirmDeleteSelected() {
  //     this.deleteSessionsDialog = false;
  //     this.sessions = this.sessions.filter(val => !this.selectedProducts.includes(val));
  //     this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Products Deleted', life: 3000 });
  //     this.selectedProducts = [];
  // }

  confirmDelete() {
      this.deleteSessionDialog = false;
      this.sessions = this.sessions.filter(val => val.id !== this.ses.id);
      this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Product Deleted', life: 3000 });
      this.ses = {};
  }

  hideDialog() {
      this.sessionDialog = false;
      this.submitted = false;
  }

  saveSession() {
    this.submitted = true;
    if (!this.ses.nom) {
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Nom est requis.' });
      return;
    }
    if (this.ses.id) {
      // Si l'identifiant de la session existe, c'est une mise à jour
      this.sessionService.putSession(this.ses.id, { nom: this.ses.nom }).subscribe(
        res => {
          this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Session mise à jour avec succès', life: 3000 });
          console.log('Session mise à jour avec succès');
          this.sessionDialog = false;
          this.getAllSession()
          this.ses = {}; // Réinitialisez l'objet ses après la mise à jour avec succès
          this.submitted = false; // Réinitialisez submitted après la mise à jour avec succès
        },
        error => {
          console.error('Erreur lors de la mise à jour de la session:', error);
          // Gérez l'erreur, par exemple, affichez un message à l'utilisateur
        }
      );
    } else {
      // Sinon, c'est une nouvelle session à ajouter
      this.sessionService.addSession(this.ses).subscribe(
        res => {
          this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Session ajoutée avec succès', life: 3000 });
          this.sessionDialog = false;
          this.getAllSession()
          console.log('Session ajoutée avec succès');
          this.ses = {}; // Réinitialisez l'objet ses après l'ajout avec succès
          this.submitted = false; // Réinitialisez submitted après l'ajout avec succès
        },
        error => {
          console.error('Erreur lors de l\'ajout de la session:', error);
          // Gérez l'erreur, par exemple, affichez un message à l'utilisateur
        }
      );
    }
  }
  
  
  
  
  
  



  createId(): string {
      let id = '';
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      for (let i = 0; i < 5; i++) {
          id += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      return id;
  }

  onGlobalFilter(table: Table, event: Event) {
      table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }

  getAllSession(): void {
    this.sessionService.getSession().subscribe(ss => {
      this.sessions = ss;
      console.log("sessions récupérés:", ss);
    });
  }
}


