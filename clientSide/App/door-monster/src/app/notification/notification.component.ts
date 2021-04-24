import { Component, OnInit } from '@angular/core';
import {Notification} from '../model/notification'
import {NotificationService} from "../services/notification.service";
import {interval, Observable, Observer} from "rxjs";
import {not} from "rxjs/internal-compatibility";
@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit , Observer<Notification>{

  errorNotifications: Notification[];
  successNotifications: Notification[];

  constructor(private notificationService: NotificationService) { }

  ngOnInit() {
    this.errorNotifications = [];
    this.successNotifications = [];
    this.notificationService.subscribe(this);
  }

  next(value: Notification) {
    value.id = this.errorNotifications.length + this.successNotifications.length;
    if (value.type == "error") {
      this.errorNotifications.push(value);
    } else {
      this.successNotifications.push(value);
    }
    this.handleNotificationLifespan(value);
  }


  onDelete(notification: Notification) {
    if (notification.type == "error") {
      const notificationErrorIndex: number = this.errorNotifications.indexOf(notification);
      this.errorNotifications.splice(notificationErrorIndex, 1);
    } else {
      const notificationSuccessIndex: number = this.successNotifications.indexOf(notification);
      this.successNotifications.splice(notificationSuccessIndex, 1);
    }
  }

  handleNotificationLifespan(notification: Notification) {
    interval(notification.lifespan * 1000).subscribe(() => this.onDelete(notification));
  }

  closed: boolean;
  complete: () => void;
  error: (err: any) => void;

}
