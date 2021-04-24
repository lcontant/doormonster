import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../../services/user.service";
import {HttpResponse} from "@angular/common/http";

@Component({
  selector: 'app-account-activate',
  templateUrl: './account-activate.component.html',
  styleUrls: ['./account-activate.component.css']
})
export class AccountActivateComponent implements OnInit {

  isActivated: boolean = false;
  activationRequestSent: boolean = false;

  constructor(private route: ActivatedRoute
              , private userService: UserService) { }

  ngOnInit() {
    this.sendActivationRequest();
  }

  sendActivationRequest() {
      let activationToken:string = this.route.snapshot.paramMap.get('activationId');
      this.activationRequestSent = true;
      this.userService.activateUser(activationToken).subscribe(response => {
          this.isActivated = true;
      },error => {
          this.isActivated = false;
      });
  }

}
