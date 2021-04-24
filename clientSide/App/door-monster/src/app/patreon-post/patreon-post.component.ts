import { Component, OnInit } from '@angular/core';
import {PatreonService} from "../services/patreon.service";
import {patreonPost} from "../model/patreonPost";
import {User} from "../model/user";
import {UserService} from "../services/user.service";

@Component({
  selector: 'app-patreon-post',
  templateUrl: './patreon-post.component.html',
  styleUrls: ['./patreon-post.component.css']
})
export class PatreonPostComponent implements OnInit {

  posts: patreonPost[];
  currentUser: User;

  constructor(private patreonService: PatreonService, private userService: UserService) { }

  ngOnInit() {
    this.userService.getUser().subscribe(response => {
        this.currentUser = response;
    });
    this.getPosts();
  }

  getPosts() {
    this.patreonService.getPosts().subscribe(response => {
      this.posts = response.data;
    })
  }

}
