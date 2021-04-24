import {Component, OnInit} from "@angular/core";
import {UserService} from "../../services/user.service";
import {User} from "../../model/user";
import {SessionService} from "../../services/session.service";
import {constants} from "../../model/constants";
import {Router} from "@angular/router";
import {ImageService} from "../../services/image.service";
import {PatreonService} from "../../services/patreon.service";
import {FeedbackService} from "../../services/feedback.service";
import {ApiService} from "../../services/api.service";
import {SupportService} from "../../services/support.service";
import {Supporter} from "../../model/supporter";
import {environment} from "../../../environments/environment";

@Component({
  selector: "app-account-edit",
  templateUrl: "./account-edit.component.html",
  styleUrls: ["./account-edit.component.css"]
})
export class AccountEditComponent implements OnInit {

  user: User;
  patreonSyncRequestSent: boolean;
  patreonSyncReponseReceived: boolean;
  patreonStatusConfirmed: boolean;
  emailSendingMessage: string;
  feedbackContent: string;
  feedbackMessage: string;
  currentSupporter: Supporter;
  emailConfirmationRequestResponse: string;
  PatreonButtonMessage: string;
  passwordsAreTheSame: boolean;

  constructor(private userService: UserService
    , private constants: constants
    , private sessionService: SessionService
    , private router: Router
    , private imageService: ImageService
    , private patreonService: PatreonService
    , private feedbackService: FeedbackService
    , private apiService: ApiService
    , private supportService: SupportService) {
  }

  ngOnInit() {
    this.patreonSyncRequestSent = false;
    this.patreonSyncReponseReceived = false;
    this.passwordsAreTheSame = false;
    this.patreonStatusConfirmed = false;
    this.emailSendingMessage = "";
    this.PatreonButtonMessage = "Sync with patreon";
    this.feedbackMessage = "SEND IT!";
    this.emailConfirmationRequestResponse = "";
    this.getUser();
  }

  getRedirectHostname() {
    return environment.patreonRedirect;
  }

  getPatreonClientId() {
    return environment.patreonClientId;
  }

  onUserStatusChanged() {
    this.getUser();
  }

  private getUser() {
    this.userService.getUser().subscribe(user => {
        this.user = user;
        if (this.user == undefined || this.user == null) {
          this.router.navigateByUrl("/account/login");
        } else {
          this.patreonStatusConfirmed = this.user.patreonContribution > 0;
          if (this.patreonStatusConfirmed) {
            this.PatreonButtonMessage = "Congrats you're now a patron";
          } else {
            this.updatePatreonStatus();
          }
        }
        this.getCurrentSupporter();
      },
      error => {
        this.router.navigateByUrl("/account/login");
      });
  }

  getCurrentSupporter() {
     this.supportService.getCurrent().subscribe(supporter => {
        this.currentSupporter = supporter;
     }, error => {
        this.currentSupporter = undefined;
     });
  }

  sendFeedback() {
    this.feedbackMessage = "Sending it... hold on";
    this.feedbackService.sendUserFeedback(this.feedbackContent).subscribe(response => {
      this.feedbackMessage = "Thank you for your feedback!";
      this.feedbackContent = "";
    }, error => {
      if (error.error) {
        this.feedbackMessage = error.error;
      } else {
        this.feedbackMessage = "";
      }
    });
  }


  updatePatreonStatus() {
    this.patreonSyncRequestSent = true;
    this.patreonService.currentUserIsPatron().subscribe(response => {
      this.patreonSyncReponseReceived = true;
      this.patreonStatusConfirmed = response;
    })
  }
}
