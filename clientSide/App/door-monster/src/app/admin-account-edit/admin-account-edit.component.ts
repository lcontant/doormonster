import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../services/user.service";
import {User} from "../model/user";
import {ImageService} from "../services/image.service";
import {isDefined} from "@angular/compiler/src/util";
import {constants} from "../model/constants";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-admin-account-edit',
  templateUrl: './admin-account-edit.component.html',
  styleUrls: ['./admin-account-edit.component.css']
})
export class AdminAccountEditComponent implements OnInit {

  private currentUserId: number;

  currentUser: User;
  currentImage: string;
  userUpdateRequestSent: boolean;
  userUpdateRequestSuccessfull: boolean;

  constructor(private route: ActivatedRoute, private userService: UserService, private imageService: ImageService, private notificationService: NotificationService) {

  }

  ngOnInit() {
    this.currentUserId = Number(this.route.snapshot.paramMap.get("userId"));
    this.userService.getUserById(this.currentUserId).subscribe(user => {
      this.currentUser = user;
    });
  }

  onUserImageUpdate($event) {
    this.currentUser.avatar = $event.target.files[0];
    this.setCurrentImage();
  }

  profileImageIsToBig() {
    return isDefined(this.currentUser.avatar) && this.currentUser.avatar.size > constants.PROFILE_IMAGE_SIZE_LIMIT;
  }

  private setCurrentImage() {
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent) => {
      this.currentImage = <string>reader.result;
      this.currentUser.avatar = this.imageService.toBlob(this.currentImage);
    };
    reader.readAsDataURL(this.currentUser.avatar);
  }

  formIsValid() {
    const isValid: boolean = isDefined(this.currentUser)
      && isDefined(this.currentUser.username) && this.currentUser.username.length > 0
      && isDefined(this.currentUser.location) && this.currentUser.location.length > 0
      && isDefined(this.currentUser.email) && this.currentUser.email.length > 0
      && !this.profileImageIsToBig()
      && (this.currentUser.avatar && this.currentUser.avatar.size < constants.PROFILE_IMAGE_SIZE_LIMIT)
      || !this.currentUser.avatar;
    return isValid;
  }

  edit() {
    this.userUpdateRequestSent = true;
    this.userService.updateUser(this.currentUser).subscribe(response => {
      if (this.currentUser.avatar) {
        this.userService.uploadAvatar(this.currentUser, this.currentUser.avatar.name).subscribe(innerReponse => {
          this.userService.onUserUpdate();
          this.userUpdateRequestSuccessfull = true;
        }, error => {
          this.userUpdateRequestSuccessfull = false;
        });
      }
      this.userUpdateRequestSuccessfull = true;
    }, error => {
      this.userUpdateRequestSuccessfull = false;
    });
  }

  banUser() {
    this.userService.banUserById(this.currentUser.userId).subscribe(userwasbanned => {
      this.currentUser.isBanned = userwasbanned;
    });
  }

  unBanUser() {
    this.userService.unBanUserById(this.currentUser.userId).subscribe(user_was_unbanned => {
      this.currentUser.isBanned = !user_was_unbanned;
    });
  }

  resendVerification() {
    this.userService.resendVerificationFromAdmin(this.currentUser.userId).subscribe(success => {
        this.notificationService.createSuccessNotification(success);
      },
      error => {
        this.notificationService.createErrorNotification(error.error);
      });
  }
}
