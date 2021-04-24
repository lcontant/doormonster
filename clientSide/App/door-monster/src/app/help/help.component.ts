import {Component, OnInit, ViewChild} from '@angular/core';
import {MailService} from "../services/mail.service";
import {buffer} from "rxjs/operators";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.css']
})
export class HelpComponent implements OnInit {

  @ViewChild("reportModal") reportModal: any;
  submitButtonDisabled: boolean;
  bugReport: string;
  submitButtonMessage: string;
  requestSent: boolean;
  submitButtonClass: string;

  constructor(private mailService: MailService, private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.initUI();
  }

  private initUI() {
    this.submitButtonDisabled = false;
    this.submitButtonMessage = "Submit";
    this.submitButtonClass = "";
    this.bugReport = "";
    this.requestSent = false;
  }

  toggleModal() {
    if (this.reportModal.nativeElement.classList.contains("is-active")) {
      this.reportModal.nativeElement.classList.remove("is-active");
      this.initUI();
    } else {
      this.reportModal.nativeElement.classList.add("is-active");
    }
  }


  sendReport() {
    this.submitButtonDisabled = true;
    this.submitButtonMessage = "Sending your mail...";
    this.mailService.reportBug(this.bugReport).subscribe(success => {
      this.notificationService.createSuccessNotification("We got your report!");
      this.submitButtonClass = "success";
      this.submitButtonMessage = "We got your report!";
      this.submitButtonDisabled = false;
    }, error => {
      this.notificationService.createErrorNotification("There was a problem while sending the report." +
        "If this occurs again please email contact@doormonster.tv directly");
      this.submitButtonMessage = "There was a problem while sending the report.";
      this.submitButtonClass = "error";
      this.submitButtonDisabled = false;
    });
  }

}
