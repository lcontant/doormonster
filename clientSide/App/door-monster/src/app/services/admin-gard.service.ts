import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import {UserService} from "./user.service";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AdminGard implements CanActivate {

  constructor(private userService: UserService) {

  }
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.userService.getCurrentRole().pipe(map(role => {
      return role && role.name == "ADMIN";
    }));
  }
}
