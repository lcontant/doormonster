import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {User} from "../../model/user";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-account-password-edit',
  templateUrl: './account-password-edit.component.html',
  styleUrls: ['./account-password-edit.component.css']
})
export class AccountPasswordEditComponent implements OnInit {

  @Input() user: User;
  @Output() userChange: EventEmitter<User> = new EventEmitter<User>();

  oldPassword: string;
  newPassword: string;
  newPasswordConfirmation: string;
  passwordFormIsValid: boolean;
  passwordUpdateSuccessful: boolean;
  passwordUpdateRequestSent: boolean;
  passwordsAreTheSame: boolean;

  constructor(private userService: UserService) { }

  ngOnInit() {
  }
  validatePasswordForm() {
    this.passwordFormIsValid = this.newPassword == this.newPasswordConfirmation && this.oldPassword && this.oldPassword.length > 0;
    this.passwordsAreTheSame = this.newPassword != this.newPasswordConfirmation;
  }

  sendPasswordUpdate() {
    this.userService.updatePassword(this.oldPassword, this.newPassword).subscribe((response) => {
      this.passwordUpdateSuccessful = true;
      this.passwordUpdateRequestSent = true;
      this.userChange.emit(this.user);
    }, error => {
      this.passwordUpdateRequestSent = true;
      this.passwordUpdateSuccessful = false;
    });
  }

}
