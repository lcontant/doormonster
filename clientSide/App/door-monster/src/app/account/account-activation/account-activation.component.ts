import { Component, OnInit } from '@angular/core';
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-account-activation',
  templateUrl: './account-activation.component.html',
  styleUrls: ['./account-activation.component.css']
})
export class AccountActivationComponent implements OnInit {
  emailSendingMessage: string;
  constructor(private userService: UserService) { }

  ngOnInit() {
  }

  resendConfirmationEmail() {
    this.userService.resendVerificationEmail().subscribe(response => {
      this.emailSendingMessage = "We sent the email to the address specified in your account. " +
        "If you don't receive anything make sure you put the right email in there";
    }, error => {
      this.emailSendingMessage = "There was an error while sending your email. Please check that there aren't any typos in it";
    });
  }
}
