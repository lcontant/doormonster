import {Component, OnInit} from '@angular/core';
import {UserService} from "../services/user.service";
import {ActivatedRoute} from "@angular/router";
import {User} from "../model/user";
import {NotificationService} from "../services/notification.service";
import {Comment} from "../model/comment";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  currentUserId: number;
  currentUser: User;
  comments: Comment[];

  constructor(private route: ActivatedRoute
    , private userService: UserService
    , private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.getUser();
    this.route.params.subscribe(params => {
      this.getUser();
    });
  }

  private getUser() {
    this.currentUserId = this.route.snapshot.params["userId"];
    this.userService.getUserById(this.currentUserId).subscribe(response => {
      this.currentUser = response;
    }, error => {
      this.notificationService.createErrorNotification("Error while loading user");
    });
  }

  private getComments() {

  }

}
