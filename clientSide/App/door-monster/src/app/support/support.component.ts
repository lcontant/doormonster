import { Component, OnInit } from '@angular/core';
import {SupportService} from "../services/support.service";
import {Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
declare var StripeCheckout: any;

@Component({
  selector: 'app-support',
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.css']
})
export class SupportComponent implements OnInit {

  handler: any;
  currentActiveTab: number;
  currentAmount: number;
  tierAmounts: number[] = [100, 500, 1000, 2000, 5000, 50000];
  subcriptionRequestStatusMessage: string;
  subscribedSuccessfully: boolean;

  constructor(private supportService: SupportService, private router: Router) { }

  ngOnInit() {
    this.currentAmount = 100;
    this.currentActiveTab = 0;
    this.subscribedSuccessfully = false;
    this.subcriptionRequestStatusMessage = "";
    this.handler = StripeCheckout.configure({
      key: environment.stripeKey,
      image: 'https://s3.amazonaws.com/doormonster/assets/images/Door_Monster_Seal.png',
      locale: 'auto',
      token: (token, args) =>
        {
          this.tokenHandler(token, args);
        }
    });
  }

  tokenHandler(token, args) {
    this.supportService.subscribe(token, this.currentAmount).subscribe(success => {
        this.subscribedSuccessfully = true;
        this.subcriptionRequestStatusMessage = "Request sent";
        setTimeout(() =>
          {
            this.router.navigate(['/manage']);
          },
          100);
      }
      , (error : HttpErrorResponse)  => {
        this.subscribedSuccessfully = false;
        this.subcriptionRequestStatusMessage = "We failed to add your subscription to the system for this reason: " + error.error;
      });

  }

  onCheckoutClick(event: Event) {
    const openRequestOptions = {
      name: "doormonster site",
      description: "supporter checkout widget",
      currency: "usd",
      amount: this.currentAmount
    };
    this.handler.open(openRequestOptions);
    event.preventDefault();
  }

  setActiveTab(index: number) {
    this.currentAmount = this.tierAmounts[index];
    this.currentActiveTab = index;
  }

  isActiveTab(index: number) {
    return this.currentActiveTab == index;
  }

}
