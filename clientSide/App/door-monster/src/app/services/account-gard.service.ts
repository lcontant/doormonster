import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, CanLoad, Route, UrlSegment, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import {Observable, pipe} from 'rxjs';
import {SessionService} from "./session.service";
import {UserService} from "./user.service";
import {map} from "rxjs/operators";
import {User} from "../model/user";
import {isDefined} from "@angular/compiler/src/util";

@Injectable({
  providedIn: 'root'
})
export class AccountGard implements CanActivate, CanActivateChild, CanLoad {

  constructor(private userService: UserService) {

  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.isUserLoggedIn();
  }

  canActivateChild(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.isUserLoggedIn();
  }

  canLoad(
    route: Route,
    segments: UrlSegment[]): Observable<boolean> | Promise<boolean> | boolean {
    return this.isUserLoggedIn();
  }

  private isUserLoggedIn(): Observable<boolean> {
    return this.userService.getUser()
      .pipe<boolean>(map(user => {
        return isDefined(user);
      }));
  }
}
