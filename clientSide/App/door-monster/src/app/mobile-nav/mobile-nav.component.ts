import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router, RouterEvent} from "@angular/router";

@Component({
  selector: 'app-mobile-nav',
  templateUrl: './mobile-nav.component.html',
  styleUrls: ['./mobile-nav.component.css']
})
export class MobileNavComponent implements OnInit {

  @ViewChild("navMenu") navMenu;

  constructor(private router: Router) { }

  ngOnInit() {
    this.router.events.subscribe((event: RouterEvent)=> {
      if (event instanceof NavigationEnd) {
        if (event.url && event.url.indexOf("video") != -1) {
          this.navMenu.nativeElement.classList.add("is-removed");
        } else {
          this.navMenu.nativeElement.classList.remove("is-removed");
        }
      }
    })
  }

}
