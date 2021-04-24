import {EventEmitter,Component, Input, OnInit, Output} from '@angular/core';
import {User} from "../../model/user";
import {isDefined} from "@angular/compiler/src/util";
import {constants} from "../../model/constants";
import {UserService} from "../../services/user.service";
import {ImageService} from "../../services/image.service";
import {SessionService} from "../../services/session.service";
import {Router} from "@angular/router";
import {isUndefined} from "util";

@Component({
  selector: 'app-account-info-edit',
  templateUrl: './account-info-edit.component.html',
  styleUrls: ['./account-info-edit.component.css']
})
export class AccountInfoEditComponent implements OnInit {

  @Input() user: User;
  @Output() userChange: EventEmitter<User> = new EventEmitter<User>();

  currentFileName: string;
  currentImage: string;
  userUpdateRequestSent: boolean;
  userUpdateRequestSuccessfull: boolean;
  userUpdateResult: string;
  waitingForUserUpdateAnswer: boolean;
  userUpdateErrorMessage: string;

  constructor(private userService: UserService
              , private imageService: ImageService
              , private sessionService: SessionService
              , private router: Router) { }

  ngOnInit() {
    this.currentFileName = "";
    this.waitingForUserUpdateAnswer = false;
    this.subscribeToUserService();
  }

  profileImageIsToBig() {
    return isDefined(this.user.avatar) && this.user.avatar.size > constants.PROFILE_IMAGE_SIZE_LIMIT;
  }

  updateUserPicture() {
    this.userService.getUser().subscribe(response => {
      this.user = response;
    });
  }

  private subscribeToUserService() {
    this.userService.subscribe(this.updateUserPicture);
  }


  onAvatarChange($event) {
    this.user.avatar = $event.target.files[0];
    this.setCurrentImage();
  }

  private setCurrentImage() {
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent) => {
      this.currentFileName = this.user.avatar.name;
      this.currentImage = <string>reader.result;
      this.user.avatar = this.imageService.toBlob(this.currentImage);
    };
    reader.readAsDataURL(this.user.avatar);
  }
  edit() {
    this.userUpdateRequestSent = true;
    this.waitingForUserUpdateAnswer = true;
    this.userService.updateUser(this.user).subscribe(response => {
      if (this.user.avatar && this.user.avatar.size) {
        this.userService.uploadAvatar(this.user, this.currentFileName).subscribe(innerReponse => {
          this.userService.onUserUpdate();
          this.userUpdateRequestSuccessfull = true;
          this.sessionService.updateSession();
        }, error => {
          this.userUpdateRequestSuccessfull = false;
          if (error.error) {
            this.userUpdateErrorMessage = error.error;
          } else {
            this.userUpdateErrorMessage = "There was an unexpected errror updating your profile picture";
          }
          this.waitingForUserUpdateAnswer = false;
        });
      }
      this.userUpdateRequestSuccessfull = true;
      this.waitingForUserUpdateAnswer = false;
    }, error => {
      this.userUpdateRequestSuccessfull = false;
      this.waitingForUserUpdateAnswer = false;
      if (error.error) {
        this.userUpdateErrorMessage = error.error;
      } else {
        this.userUpdateErrorMessage = "There was an unexpected error while updating your account"
      }
    });
  }

  logout() {
    this.userService.logout().subscribe(response => {
      this.user = undefined;
      this.sessionService.stopSession();
      this.router.navigateByUrl("/home");
    }, error => {

    });
  }

  formIsValid(): boolean {
    const isValid: boolean = isDefined(this.user)
      && !isUndefined(this.user.username)
      && !isUndefined(this.user.email)
      && (!isDefined(this.user.avatar) || isDefined(this.user.avatar)
        && !isDefined(this.user.avatar.size) || this.user.avatar.size < constants.PROFILE_IMAGE_SIZE_LIMIT);
    return isValid;
  }
}
