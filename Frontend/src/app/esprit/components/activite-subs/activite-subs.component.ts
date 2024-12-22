import { Component, OnInit } from '@angular/core';
import { MessageService, SelectItem } from 'primeng/api';
import { DataView } from 'primeng/dataview';
import { Product } from 'src/app/esprit/api/product';
import { ProductService } from 'src/app/esprit/service/product.service';
import { Activite } from '../../models/activite';
import { ActiviteService } from '../../service/activite.service';
import { SessionService } from '../../service/session.service';
import { Session } from '../../models/session';
import { StorageService } from '../../service/storage.service';

@Component({
  providers: [MessageService],
  templateUrl: './activite-subs.component.html',
})
export class ActiviteSubsComponent {
  activites: Activite[] = [];

  sessions: Session[] = [];

  sortOptions: SelectItem[] = [];

  sortOrder: number = 0;

  sortField: string = '';

  sourceCities: any[] = [];

  targetCities: any[] = [];

  orderCities: any[] = [];

  isLoggedIn = false;
  
  constructor( private activiteService: ActiviteService, private sessionService: SessionService,private storageService: StorageService) { }

  ngOnInit() {
      this.getAllSessions();
      this.getAllActivites()
      this.isLoggedIn = this.storageService.isLoggedIn();
    }

  onSortChange(event: any) {
      const value = event.value;

      if (value.indexOf('!') === 0) {
          this.sortOrder = -1;
          this.sortField = value.substring(1, value.length);
      } else {
          this.sortOrder = 1;
          this.sortField = value;
      }
  }

  onFilter(dv: DataView, event: Event) {
    dv.filter((event.target as HTMLInputElement).value);
}

getAllActivites(): void {
  this.activiteService.getActivite().subscribe(ss => {
    this.activites = ss;
    console.log("activites récupérées:", ss);
  });
}

formatAnnimateur(annimateur: string): string {
  // Remplacez les tirets par des espaces
  return annimateur.replace(/_/g, ' ');
}

getAllSessions(): void {
  this.sessionService.getSession().subscribe(ss => {
    this.sessions = ss;
    console.log("Sessions récupérées:", ss);
    // this.selectedSessions = ss; // Mettez à jour selectedSessions avec les sessions récupérées
  });
}

getImageUrl(imageName: string): string {
  // console.log("imageName:" , imageName)
  return `http://localhost:9090/activite/image/${imageName}`;
}

  participateInActivity(activiteId: number): void {
    if (this.isLoggedIn) {
      const userId = this.storageService.getIdUser();
      console.log("ID de l'utilisateur:", userId); // Affichez l'ID de l'utilisateur pour le débogage
      this.activiteService.participateInActivity(activiteId,userId).subscribe(
        (response) => {
          console.log("Participation réussie:", response);
          // Mettez à jour votre UI si nécessaire
        },
        (error) => {
          console.error("Erreur lors de la participation:", error);
          // Gérez les erreurs et affichez un message à l'utilisateur si nécessaire
        }
      );
    } else {
      console.error("L'utilisateur n'est pas connecté.");
      // Gérez le cas où l'utilisateur n'est pas connecté
    }
  }

}
