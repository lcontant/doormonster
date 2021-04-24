import { Component, OnInit } from '@angular/core';
import {User} from "../model/user";
import {Role} from "../model/role";
import {UserService} from "../services/user.service";
import {SessionService} from "../services/session.service";
@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  currentRole: Role;
  currentUser: User;

  constructor(private userService: UserService,private sessionService: SessionService) { }

  ngOnInit() {
    this.getCurrentUser();
    this.getUserRole();
    this.sessionService.subscribe(() => {
      this.getUserRole();
    })
  }

  private getUserRole() {
    this.userService.getCurrentRole().subscribe(role => {
      this.currentRole = role;
    },
      error => {
        this.currentRole = undefined;
      });
  }
  private getCurrentUser() {
    this.userService.getUser().subscribe(success => {
      this.currentUser = success;
    }, error => {

    });
  }

  getDate(): number {
    return (new Date()).getFullYear();
  }


  isAdmin() {
    return this.currentRole && this.currentRole.name == 'ADMIN';
  }
}
