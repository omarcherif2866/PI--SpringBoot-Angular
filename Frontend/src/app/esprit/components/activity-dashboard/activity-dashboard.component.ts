import { Component } from '@angular/core';
import { ActiviteService } from '../../service/activite.service';
import { SessionService } from '../../service/session.service';

@Component({
  selector: 'app-activity-dashboard',

  templateUrl: './activity-dashboard.component.html',
  styleUrl: './activity-dashboard.component.scss'
})
export class ActivityDashboardComponent {
  barData1: any;
  barOptions1: any;
  totalActivites: number;
  totalSessions: number;

  barData2: any;
  barOptions2: any;
  pieData: any = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: [],
      hoverBackgroundColor: []
    }]
  };
    pieOptions: any;
    mostPopularActivity: any;
    sessionsData: any;
    constructor(private activiteService: ActiviteService,private sessionService: SessionService) {}

    ngOnInit() {
        
      this.getActivityParticipationCount();
      this.getMostPopularActivity();
      this.getActivitySessionsCount();
      this.fetchTotalActivites();
      this.fetchTotalSessions();

      this.loadPieChartData();


  }

  getActivityParticipationCount(){
      this.activiteService.getActivityParticipationCount().subscribe((data: any[]) => {
    // Structurez les données pour le graphique à barres
    this.barData1 = {
        labels: data.map(item => item[0]), // Première valeur de chaque sous-array
        datasets: [
            {
                label: 'Nombre de participations',
                data: data.map(item => item[1]), // Deuxième valeur de chaque sous-array
                backgroundColor: 'rgba(54, 162, 235, 0.6)', // Couleur de fond des barres
                borderColor: 'rgba(54, 162, 235, 1)', // Couleur de bordure des barres
                borderWidth: 1
            }
        ]
    };

    // Options du graphique à barres
    this.barOptions1 = {
        legend: {
            display: true,
            labels: {
                fontSize: 16 // Taille de la police pour la légende
            }
        },
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            xAxes: [{
                ticks: {
                    fontSize: 14 // Taille de la police pour l'axe des X
                }
            }],
            yAxes: [{
                ticks: {
                    fontSize: 14, // Taille de la police pour l'axe des Y
                    beginAtZero: true // Commence à zéro sur l'axe des Y
                    
                }
            }]
        }
    };
});
  }


  getMostPopularActivity() {
    this.activiteService.getMostPopularActivity().subscribe((data: any[]) => {
        // Vérifiez que les données sont correctement formatées
        if (data && data.length > 0) {
            this.mostPopularActivity = {
                nom: data[0][0], // Nom de l'activité
                participants: data[0][1] // Nombre de participants
            };
        }
    });
}

getActivitySessionsCount(){
  this.activiteService.getActivitySessionsCount().subscribe((data: any[]) => {
    // Formatage des données pour le Bar Chart
    if (data && data.length > 0) {
        this.sessionsData = data.map(item => ({
            activite: item[0],
            sessions: item[1]
        }));
  
        // Préparation des données pour le Bar Chart
        this.barData2 = {
            labels: this.sessionsData.map(item => item.activite),
            datasets: [
                {
                    label: 'Nombre de Sessions',
                    backgroundColor: '#42A5F5',
                    data: this.sessionsData.map(item => item.sessions),
                    
                }
            ]
        };
  
        // Options du Bar Chart
        this.barOptions2 = {
            responsive: true,
            legend: {
                position: 'top',
            },
            scales: {
                xAxes: [{
                    ticks: {
                        autoSkip: false
                    }
                }],
                yAxes: [{
                    ticks: {
                        beginAtZero: true,
                        
                    }
                }]
            }
        };
    }
  });
}

fetchTotalActivites(): void {
  this.activiteService.getCountAllActivites().subscribe(
    (count: number) => {
      this.totalActivites = count;
    },
    (error) => {
      console.error('Erreur lors de la récupération du nombre total d\'activités :', error);
    }
  );
}

fetchTotalSessions(): void {
  this.sessionService.getCountAllSessions().subscribe(
    (count: number) => {
      this.totalSessions = count;
    },
    (error) => {
      console.error('Erreur lors de la récupération du nombre total des sessions :', error);
    }
  );
}

loadPieChartData() {
  this.activiteService.getTotalBudgetPerActivity().subscribe(
    (data: any[]) => {
      if (data && data.length > 0) {
        this.pieData = {
          labels: data.map(item => item[0]), // Labels : Noms des activités
          datasets: [{
            data: data.map(item => item[1]), // Données : Budget total pour chaque activité
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'], // Couleurs des sections du Pie Chart
            hoverBackgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'] // Couleurs au survol
          }]
        };
      }
    },
    error => {
      console.error('Error fetching pie chart data: ', error);
    }
  );
}


}


