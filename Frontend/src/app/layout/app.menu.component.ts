import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { LayoutService } from './service/app.layout.service';
import { StorageService } from '../esprit/service/storage.service';
import { UserRole } from '../esprit/models/role';


@Component({
    selector: 'app-menu',
    templateUrl: './app.menu.component.html'
})
export class AppMenuComponent implements OnInit {

    model: any[] = [];
    

    constructor(public layoutService: LayoutService, private storageService: StorageService) { }

    ngOnInit() {
        this.model = [
            {
                label: 'Home',
                items: [
                    { label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/'] }
                ]
            },
            {
                label: 'Reservation',
                items: [
                    { label: 'Reservation', icon: 'pi pi-chart-line', routerLink: ['/reservation'] }
                ]
            },
            {
                label: 'Sessions',
                items: [
                    { label: 'Sessions', icon: 'pi pi-fw pi-calendar-plus', routerLink: ['/session'] }
                ]
            },
            {
                label: 'Activités',
                items: [
                    { label: 'Activités', icon: 'pi pi-fw pi-users', routerLink: ['/activite'] }
                ]
            },
            {
                label: 'Dashboard des activités',
                items: [
                    { label: 'Dashboard des activités', icon: 'pi pi-fw pi-users', routerLink: ['/activityDashboard'] }
                ]
            },
            {
                label: 'Participer à un team building',
                items: [
                    { label: 'Participer à un team building', icon: 'pi pi-fw pi-users', routerLink: ['/participerActivite'] }
                ]
            },
           
        ];
        if (this.storageService.getUser().role === UserRole.ADMIN) {
            this.model.push({
                label: 'Users',
                items: [
                    { label: 'Users', icon: 'pi pi-user', routerLink: ['/users'] },
                    { label: 'UserCharts', icon: 'pi pi-chart-line', routerLink: ['/UserCharts'] }
                ]
            });
        }
    }
}
