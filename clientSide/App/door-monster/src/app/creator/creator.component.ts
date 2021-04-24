import {Component, OnInit, ViewChild} from '@angular/core';
import {FeedbackService} from "../services/feedback.service";
import {User} from "../model/user";
import {SessionService} from "../services/session.service";
import {UserService} from "../services/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-creator',
  templateUrl: './creator.component.html',
  styleUrls: ['./creator.component.css']
})
export class CreatorComponent implements OnInit {

  @ViewChild("accountModal") accountModal: any;

  feedbackText: string;
  currentUser: User;
  serverResponse: string;
  requestSent: boolean;
  serverResponseSuccess: boolean;
  feedbackMessage: string;
  sendingFeedback: boolean;  

  constructor(private feedbackService: FeedbackService, private sessionService: SessionService, private userService: UserService, private router : Router) { }

  ngOnInit() {
    this.sendingFeedback = false;
    this.feedbackMessage = "SEND IT";
    this.serverResponseSuccess = false;
    this.userService.getUser().subscribe(response => {
      this.currentUser = response;
    }, error => {
      this.currentUser = null;
    })
  }

  sendFeedback() {
    if (this.currentUser && !this.sendingFeedback && this.feedbackMessage && this.feedbackMessage.length > 0) {
      this.sendingFeedback = true;
      this.feedbackMessage = "Sending it hold on ...";
      this.feedbackService.sendCreatorSuggestion(this.feedbackText).subscribe(response => {
        this.requestSent = true;
        this.sendingFeedback = false;
        this.serverResponseSuccess = true;
        this.serverResponse = "Feedback sent!";
        this.feedbackMessage = this.serverResponse;
        this.feedbackText = "";
      }, error => {
        this.requestSent = true;
        this.sendingFeedback = false;
        this.serverResponseSuccess = false;
        this.serverResponse = "There was a problem sending the feedback, sorry:(";
        this.feedbackMessage = this.serverResponse;
      });
    } else if(!this.currentUser){
      this.showModal();
    }
  }

  closeModal() {
    this.accountModal.nativeElement.classList.remove("show");
  }

  private showModal() {
    this.accountModal.nativeElement.classList.add("show");
  }

  GoToCreateAnAccount() {
    this.router.navigateByUrl("/account/create");
  }

}
