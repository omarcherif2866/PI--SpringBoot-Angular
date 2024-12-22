import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { StorageService } from './../service/storage.service';  // Assurez-vous d'avoir un service de stockage pour gérer le token

@Injectable({
    providedIn: 'root'
})
export class UserGuard implements CanActivate {

    constructor(private router: Router, private storageService: StorageService) { }

    canActivate(): boolean {
        const token = this.storageService.getAccessToken();  // Method to get the token
        if (token) {
            // User is authenticated, allow navigation to the requested route
            return true;
        } else {
            // User is not authenticated, navigate to the login page
            this.router.navigate(['/auth/login']);
            return false;  // Prevent navigation to the requested route
        }
    }
}
