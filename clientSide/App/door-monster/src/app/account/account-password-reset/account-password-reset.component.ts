import { Component, OnInit } from '@angular/core';
import {UserService} from "../../services/user.service";
import {ActivatedRoute} from "@angular/router";
import {NotificationService} from "../../services/notification.service";

@Component({
  selector: 'app-account-password-reset',
  templateUrl: './account-password-reset.component.html',
  styleUrls: ['./account-password-reset.component.css']
})
export class AccountPasswordResetComponent implements OnInit {

  newPassword: string;
  newPasswordConfirmation: string;
  token: string;
  passwordsAreTheSame: boolean;
  feedbackMessage: string;
  passwordReset: boolean;

  constructor(private userService: UserService, private route: ActivatedRoute, private notificationService: NotificationService) { }

  ngOnInit() {
    this.token = this.route.snapshot.paramMap.get('resetToken');
    this.newPassword = "";
    this.newPasswordConfirmation = "";
    this.passwordReset = false;
  }

  formIsValid(): boolean {
    this.passwordsAreTheSame = this.newPasswordConfirmation == this.newPassword;
    return this.passwordsAreTheSame && this.newPassword.length > 0 && this.newPassword.length > 0 && !this.passwordReset;
  }


  sendPasswordReset() {
      this.userService.resetPassword(this.token, this.newPassword).subscribe(
        response => {
          this.feedbackMessage = response;
          this.passwordReset = true;
          this.notificationService.createSuccessNotification("Susccesfully reset the password")
          },
          error => {
          this.feedbackMessage = error.error;
            this.notificationService.createErrorNotification("Error " + error.error + " encountered while trying to reset your password")
          }
        );
  }

}
