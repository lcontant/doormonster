import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, CanLoad, Route, UrlSegment, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import {SupportService} from "./support.service";
import {map} from "rxjs/operators";
import {isDefined} from "@angular/compiler/src/util";

@Injectable({
  providedIn: 'root'
})
export class NoSubscriptionGuard implements CanActivate, CanActivateChild, CanLoad {

  constructor(private supportService: SupportService) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.hasNoActiveSubscription();
  }
  canActivateChild(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.hasNoActiveSubscription();
  }
  canLoad(
    route: Route,
    segments: UrlSegment[]): Observable<boolean> | Promise<boolean> | boolean {
    return this.hasNoActiveSubscription();
  }

  hasNoActiveSubscription(): Observable<boolean> {
    return this.supportService.getCurrent().pipe<boolean>(map(supporter => {
      return !isDefined(supporter);
    }));
  }
}
