import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {PatreonService} from "../../services/patreon.service";
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'app-patreon-redirect',
  templateUrl: './patreon-redirect.component.html',
  styleUrls: ['./patreon-redirect.component.css']
})
export class PatreonRedirectComponent implements OnInit {

  constructor(private route: ActivatedRoute, private patreonService: PatreonService, private router: Router, private cookieService: CookieService) { }

  ngOnInit() {
    this.getQueryParams();
  }

  private getQueryParams() {
      let code:string = this.route.snapshot.queryParamMap.get("code");
      let state:string = this.route.snapshot.queryParamMap.get("state");
      console.log("code: " + code + " | state: " + state);
      this.patreonService.acquireTokenForUser(code).subscribe(() => {
        this.patreonService.currentUserIsPatron().subscribe((response) => {
          this.router.navigateByUrl("/account/edit");
        });
      });
  }

}
