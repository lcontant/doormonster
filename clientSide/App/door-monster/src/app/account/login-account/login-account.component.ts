import { Component, OnInit } from "@angular/core";
import {User} from "../../model/user";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {SessionService} from "../../services/session.service";

@Component({
  selector: "app-login-account",
  templateUrl: "./login-account.component.html",
  styleUrls: ["./login-account.component.css"]
})
export class LoginAccountComponent implements OnInit {

  botValue: string;
  user: User;
  isAuthenticated: boolean;
  errorMessage: string;
  constructor(private userService: UserService, private router: Router, private sessionService: SessionService) { }

  ngOnInit() {
    this.user = new User();
    this.userService.getUser().subscribe(response => {
        if (response != null) {
          this.router.navigateByUrl("/account/edit");
        }
    },
      error => {

      })
  }

  signIn() {
    if (this.botValue && this.botValue.length > 0) {
      return;
    }
    this.userService.authenticateUser(this.user).subscribe(response => {

          this.sessionService.startSession(response.sessionId);
          this.router.navigateByUrl("account/edit");

    }, (error: any) => {
      if (typeof error.error == "string") {
        this.errorMessage = error.error;
      } else {
        this.errorMessage = "An unexpected error occured while trying to authenticate you. Please try again later";
      }

    });
  }


}
