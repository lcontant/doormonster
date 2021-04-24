import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../services/user.service";

@Component({
  selector: 'app-discord-redirect',
  templateUrl: './discord-redirect.component.html',
  styleUrls: ['./discord-redirect.component.css']
})
export class DiscordRedirectComponent implements OnInit {

  constructor(private route: ActivatedRoute,private router: Router, private userService: UserService) { }

  ngOnInit() {

    let code: string = this.route.snapshot.queryParamMap.get("code");
    this.userService.linkDiscord(code).subscribe(success => {
        this.router.navigateByUrl("/manage");
    },
      error => {
        this.router.navigateByUrl("/manage");
      })
  }

}
