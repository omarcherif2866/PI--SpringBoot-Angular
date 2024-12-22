import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { StorageService} from "../service/storage.service";
import { UserRole} from "../models/role";


export class adminGuard implements CanActivate {
  constructor(private storageService: StorageService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.storageService.getUser().role === UserRole.ADMIN) {
      return true;
    } else {
      this.router.navigate(['/access-denied']);
      return false;
    }
  }
}