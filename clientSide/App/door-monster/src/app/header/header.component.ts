import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {User} from "../model/user";
import {UserService} from "../services/user.service";
import {SessionService} from "../services/session.service";
import {Role} from "../model/role";
declare var toggleSideBar: any;
import "../../assets/scripts/sidebar";
import {Router} from "@angular/router";
import DateTimeFormat = Intl.DateTimeFormat;

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit  {
  @Input() isSidebarToggled: boolean;
  @Output() isSidebarToggledChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  currentUser: User;
  currentRole: Role;
  userImagePath: string;
  number: number;
  @ViewChild("userImage")
  userImageRef: ElementRef;

  userImage: HTMLImageElement;

  currentUserIsAdmin: boolean;
  profileNotLoaded: boolean;
  constructor(private userService: UserService, private sessionService: SessionService, private router: Router) { }

  ngOnInit() {
    this.isSidebarToggled = false;
    this.profileNotLoaded = false;
    this.number = 768;
    this.getUser();
    this.currentUserIsAdmin = false;
    this.sessionService.subscribe(() => {
      this.getUser();
      this.profileNotLoaded = false;
    });
    this.router.events.subscribe(() => {
      if (this.isSidebarToggled) {
        this.sideBarToggle();
      }
    })
  }

  errorProfileLoading(error) {
    this.profileNotLoaded = true;
    console.log(error)
  }

  getUser() {
    this.userService.getUser().subscribe(response => {
        this.currentUser = response;
        this.userImagePath = "https://s3.amazonaws.com/doormonster/assets/images/user/" + this.currentUser.avatar + "?" + Date.now().toString() + Math.random();
        if (this.currentUser) {
          this.userService.getCurrentRole().subscribe(response => {
            this.currentRole = response;
            this.currentUserIsAdmin = this.currentRole && this.currentRole.name == "ADMIN";
          });
        } else {
          this.userImagePath = "";
        }
      },
      error => {
        this.currentUser = undefined;
      });
  }


 sideBarToggle() {
   toggleSideBar(this.isSidebarToggled);
   this.isSidebarToggled = !this.isSidebarToggled;
   this.isSidebarToggledChange.emit(this.isSidebarToggled);
 }

 toggleLogo(searchBarToggled: Event) {
 }


}
