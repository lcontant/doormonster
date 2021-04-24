import { Component, OnInit } from '@angular/core';
import {SupportService} from "../../services/support.service";
import {cards} from "stripe";
import {environment} from "../../../environments/environment";
declare var StripeCheckout: any;

@Component({
  selector: 'app-support-payment-edit',
  templateUrl: './support-payment-edit.component.html',
  styleUrls: ['./support-payment-edit.component.css']
})
export class SupportPaymentEditComponent implements OnInit {

  supporterCardList: cards.ICard[];
  cardFetchingStatus: string;
  handler: any;

  constructor(private supportService: SupportService) { }

  ngOnInit() {
    this.cardFetchingStatus = "Getting your payment info from stripe...";
    this.getCurrentSupporterCardListFromServer();
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

  private tokenHandler(token, args) {
      this.supportService.createCard(token).subscribe(success => {
        this.supporterCardList = success;
      }, error => {
        this.cardFetchingStatus = "There was an error loading your cards";
      });
  }


  onNewCardClick(event: Event) {
    const openRequestOptions = {
      name: "doormonster site",
      description: "Enter the payment method you wish for us to use",
      panelLabel: "Add a new card"
    };
    this.handler.open(openRequestOptions);
    event.preventDefault();
  }

  private getCurrentSupporterCardListFromServer() {
    this.supportService.getAllCards().subscribe(success => {
        this.supporterCardList = success;
    },
    errorResponse => {
      this.cardFetchingStatus = "There was an error fetching the cards";
    });
  }

  canDeleleteCards() {
    return this.supporterCardList.length > 1;
  }

  deleteCard(cardId: string) {
    this.supportService.deleteCard(cardId).subscribe(success => {
      this.supporterCardList = success;
    }, error => {
      this.cardFetchingStatus = "There was an error while deleting your card";
    });
  }

}
