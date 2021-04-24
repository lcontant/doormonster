import { Component, OnInit } from '@angular/core';
import {SupportService} from "../../services/support.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Router} from "@angular/router";
import {Supporter} from "../../model/supporter";
import {NotificationService} from "../../services/notification.service";
import {element} from "protractor";

@Component({
  selector: 'app-support-edit',
  templateUrl: './support-edit.component.html',
  styleUrls: ['./support-edit.component.css']
})
export class SupportEditComponent implements OnInit {

  unsubscribeServerResponse: string;
  currentTierUpgradeIndex: number;
  unsubscribedWorked: boolean;
  isShowingConfirmationModal: boolean;
  currentSupporter: Supporter;
  periodEnd: Date;

  tierAmounts: number[] = [100, 500, 1000, 2000, 5000, 50000];

  constructor(private supportService: SupportService, private router: Router, private notificationService: NotificationService) { }

  ngOnInit() {
    this.unsubscribedWorked = false;
    this.getCurrentSupporter();
  }

  getCurrentSupporter() {
    this.supportService.getCurrent().subscribe(response => {
          this.currentSupporter = response;
          this.currentTierUpgradeIndex = this.tierAmounts.indexOf(this.currentSupporter.ammount);
              this.supportService.getPeriodEnd().subscribe(periodEnd => {
                  this.periodEnd = periodEnd;
              });
    });
  }

  renew() {
   this.supportService.renew().subscribe(response => {
      this.currentSupporter.toBeCanceled = false;
   }, error => {
     this.notificationService.createErrorNotification("Problem encountered while updating");
     }
   )
  }

  canSeeTheDiscordLink() {
    return this.currentSupporter && this.currentSupporter.ammount >= 100;
  }

  unsubscribe() {
    this.supportService.unsubscribe().subscribe(success => {
      this.unsubscribedWorked = true;
      this.unsubscribeServerResponse = "You unsubscribed successfully";
      setTimeout(() =>
        {
          this.router.navigate(['/account/edit']);
        },
        10000);
    }, (error: HttpErrorResponse) => {
      this.unsubscribedWorked = false;
      this.unsubscribeServerResponse = error.error;
    });
  }

  upgradeSubscription(tier: number) {
    const ammount = this.tierAmounts[tier];
    this.toggleModal();
    this.supportService.upgradePlan(ammount).subscribe(succes => {
      this.currentSupporter = succes;
      this.notificationService.createSuccessNotification(`Tier upgrade successfull you wil be charged ${this.currentSupporter.ammount / 100}$ on ${this.periodEnd}`, 20);
    },
    error => {
      this.notificationService.createErrorNotification("We encountered an error while trying to change your subscription", 20);
    });
  }

  getTierIndex(): number {
    return this.currentSupporter && this.tierAmounts.indexOf(this.currentSupporter.ammount);
  }

  canSeeScript(): boolean {
    return this.currentSupporter && this.currentSupporter.ammount > 700;
  }

  shouldSeeUnsubscribe(): boolean {
    return this.currentSupporter && !this.currentSupporter.toBeCanceled;
  }

  shouldSeeRenewSubscription(): boolean{
    return this.currentSupporter && this.currentSupporter.toBeCanceled;
  }


  setActiveTab(tabIndex: number) {
    this.currentTierUpgradeIndex = tabIndex;
  }

  isActiveTab(tabIndex: number) {
    return this.currentTierUpgradeIndex == tabIndex;
  }

  toggleModal() {
    this.isShowingConfirmationModal = !this.isShowingConfirmationModal;
  }

  canSwitchTiers() {
    return this.currentSupporter && this.currentSupporter.ammount != this.tierAmounts[this.currentTierUpgradeIndex];
  }

}
