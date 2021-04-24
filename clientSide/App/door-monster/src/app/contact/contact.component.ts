import { Component, OnInit } from '@angular/core';
import {MailService} from "../services/mail.service";

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {
  body: string;
  sender: string;
  email: string;
  emailSendingResponse: string;
  emailResponseReceived: boolean;
  emailSuccess: boolean;
  sendingMail: boolean;
  sendingButtonValue: string;
  senderError: boolean;
  emailError: boolean;
  bodyError: boolean;

  constructor(private mailService: MailService) { }

  ngOnInit() {
    this.emailResponseReceived = false;
    this.emailSuccess = false;
    this.sendingMail = false;
    this.senderError = false;
    this.emailError = false;
    this.bodyError = false;
    this.sendingButtonValue = "Submit";
  }

  sendEmail() {
    this.emailError = !this.email || this.email.length <= 0;
    this.senderError = !this.sender || this.sender.length <= 0;
    this.bodyError = !this.body || this.body.length <= 0;
    if (!this.sendingMail && !this.emailError && !this.senderError && !this.bodyError) {
      this.sendingMail = true;
      this.sendingButtonValue = "Sending it...";
      this.mailService.sendMail(this.sender, this.body, this.email).subscribe((response) => {
        this.emailResponseReceived = true;
        this.sendingMail = false;
        this.emailSuccess = true;
        this.email = "";
        this.sender = "";
        this.body = "";
        this.emailSendingResponse = "We got your message!";
        this.sendingButtonValue = this.emailSendingResponse;
      }, error => {
        this.emailResponseReceived = true;
        this.sendingMail = false;
        this.emailSuccess = false;
        this.emailSendingResponse = "There was an error sending the email, sorry, please try again later";
        this.sendingButtonValue = this.emailSendingResponse;
      });
    }
  }

}
