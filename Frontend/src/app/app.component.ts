import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PrimeNGConfig } from 'primeng/api';
import { StorageService } from './esprit/service/storage.service';
@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    constructor(private primengConfig: PrimeNGConfig, private router: Router, private storageService: StorageService) { }

    ngOnInit() {
        this.primengConfig.ripple = true;
      
    }
}
