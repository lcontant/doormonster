import { Injectable } from '@angular/core';
import {Observable, Observer} from "rxjs";
import {Notification} from '../model/notification';
import {not} from "rxjs/internal-compatibility";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  observers: Observer<Notification>[];

  constructor() {
    this.observers = [];
  }

  subscribe(observer: Observer<Notification>) {
    this.observers.push(observer);
  }

  notify(notification: Notification) {
    for (let observer  of this.observers) {
      observer.next(notification);
    }
  }

  createErrorNotification(message: string, lifespan?: number) {
    let notification: Notification = new Notification();
    notification.type = "error";
    notification.message = message;
    if (lifespan) {
     notification.lifespan = lifespan;
    } else {
      notification.lifespan = 5;
    }
    this.notify(notification);
  }

  createSuccessNotification(message: string, lifespan?: number) {
    let notification: Notification = new Notification();
    notification.type = "success";
    notification.message = message;
    if (lifespan) {
      notification.lifespan = lifespan;
    } else {
      notification.lifespan = 5;
    }
    this.notify(notification);
  }

}
