import { Component, OnInit } from "@angular/core";
import {User} from "../../model/user";
import {UserService} from "../../services/user.service";
import {isUndefined} from "util";
import {SessionService} from "../../services/session.service";
import {Router} from "@angular/router";
import {ImageService} from "../../services/image.service";
import {isDefined} from "../../../../node_modules/@angular/compiler/src/util";
import {constants} from "../../model/constants";
@Component({
  selector: "app-create-account",
  templateUrl: "./create-account.component.html",
  styleUrls: ["./create-account.component.css"]
})
export class CreateAccountComponent implements OnInit {
  user: User;
  currentImage: string;
  errorMessage: string;
  passwordConfirm: string;
  TOSAccepted: boolean;
  currentFileName: string;
  sendingCreationRequest: boolean;
  accountCreated: boolean;
  ageRestrictionConfirmed: boolean;

  constructor(private userService: UserService
              , private sessionService: SessionService
              , private router: Router
              , private imageService: ImageService
              , public constants: constants) { }

  ngOnInit() {
    this.sendingCreationRequest = false;
    this.accountCreated = false;
    this.user = new User();
    this.TOSAccepted = false;
  }

  passwordsAreTheSame() {
    return (!this.user.password && !this.passwordConfirm) ||this.user.password && this.passwordConfirm && this.user.password == this.passwordConfirm;
  }

  create() {
    if (!this.sendingCreationRequest) {
      this.errorMessage = "";
      this.sendingCreationRequest = true;
      if (!this.user.location) {
        this.user.location = "";
      }
      this.userService.createUser(this.user).subscribe(response => {
          if (this.user.avatar) {
            this.userService.uploadAvatar(this.user, this.currentFileName).subscribe(innerReponse => {
              this.accountCreated = true;
              this.sendingCreationRequest = false;
            }, error => {
              this.accountCreated = true;
              this.sendingCreationRequest = false;
            });
          } else {
            this.accountCreated = true;
            this.sendingCreationRequest = false;
          }
        },
        (error) => {
        this.sendingCreationRequest = false;
          if (typeof error.error == "string") {
            this.errorMessage = error.error;
          } else {
            this.errorMessage = "An unexpected error occured, please try again later";
          }
        });
    }
  }

  profileImageIsToBig() {
    return isDefined(this.user.avatar) && this.user.avatar.size > constants.PROFILE_IMAGE_SIZE_LIMIT;
  }

  onAvatarChange($event) {
      this.user.avatar = $event.target.files[0];
      const reader = new FileReader();
    reader.onload = (e: ProgressEvent) => {
      this.currentFileName = this.user.avatar.name;
      this.currentImage = <string>reader.result;
      this.user.avatar = this.imageService.toBlob(this.currentImage);
    };
      reader.readAsDataURL(this.user.avatar);
  }

  formIsValid(): boolean {
    const isValid: boolean = isDefined(this.user)
      && !isUndefined(this.user.password)
      && isDefined(this.passwordConfirm)
      && this.user.password == this.passwordConfirm
      && !isUndefined(this.user.username)
      && !isUndefined(this.user.email)
      && (!isDefined(this.user.avatar) || isDefined(this.user.avatar)
        && this.user.avatar.size < constants.PROFILE_IMAGE_SIZE_LIMIT)
      && this.TOSAccepted
      && !this.sendingCreationRequest
      && !this.accountCreated
      && this.ageRestrictionConfirmed;
    return isValid;
  }

}
