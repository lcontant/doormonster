import { Component, OnInit } from '@angular/core';
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-password-reset-request',
  templateUrl: './password-reset-request.component.html',
  styleUrls: ['./password-reset-request.component.css']
})
export class PasswordResetRequestComponent implements OnInit {

  email: string;
  emailSent: boolean;
  emailRequestSent: boolean;
  serverResponseMessage: string;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.email = "";
    this.emailSent = false;
    this.emailRequestSent = false;
    this.serverResponseMessage = "";
  }

  formIsValid() {
    return this.email.length > 0;
  }

  sendRequest() {
    this.emailRequestSent = true;
      this.userService.sendPasswordReset(this.email).subscribe(response => {
        this.emailSent = true;
        this.serverResponseMessage = response;
      }, error => {
        this.emailSent = false;
        this.serverResponseMessage = error;
      });
  }

}
