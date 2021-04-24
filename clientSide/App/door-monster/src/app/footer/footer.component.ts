import { Component, OnInit } from '@angular/core';
import {FriendService} from "../services/friend.service";
import {Friend} from "../model/friend";

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  friends : Friend[];

  constructor(private friendService: FriendService) {

  }

  ngOnInit() {
    this.getFriends();
  }

  getFriends() {
      this.friendService.getRandomFriends().subscribe((response) => {
          this.friends = response;
      });
  }

  getDate(): number {
    return (new Date()).getFullYear();
  }

}
