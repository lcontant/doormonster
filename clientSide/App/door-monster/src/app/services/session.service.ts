import { Injectable } from "@angular/core";
import {CookieService} from "ngx-cookie-service";
import {Observable, Subject} from "rxjs";

@Injectable({
  providedIn: "root"
})
export class SessionService {

  callbacks: Function[];

  constructor(private cookieService: CookieService) {
    this.callbacks = [];
  }


  startSession(sessionId: string) {
    let expirationDate: Date = new Date();
    expirationDate.setDate(expirationDate.getDate() + 60);
    this.cookieService.set('sessionId', sessionId,expirationDate);
    this.notifyEveryone();
  }

  updateSession() {
    this.notifyEveryone();
  }

  stopSession() {
    this.cookieService.delete('sessionId');
    this.notifyEveryone();
  }

  getSessionId(): string {
   return  this.cookieService.get('sessionId');
  }

  subscribe(callback: Function) {
    this.callbacks.push(callback);
  }

  unsubscribe(callback: Function) {
   this.callbacks =  this.callbacks.filter((itCallback) => callback != itCallback);
  }



  notifyEveryone() {
    this.callbacks.forEach(callback => {
      callback();
    })
  }


}
