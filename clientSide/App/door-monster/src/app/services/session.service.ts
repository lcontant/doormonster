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
    window.localStorage.setItem('sessionId', sessionId);
    this.notifyEveryone();
  }

  updateSession() {
    this.notifyEveryone();
  }

  stopSession() {
    window.localStorage.removeItem('sessionId');
    this.notifyEveryone();
  }

  getSessionId(): string {
   return  window.localStorage.getItem('sessionId');
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
