import { Component, OnInit } from "@angular/core";
import {VideoService} from "../services/video.service";
import {Video} from "../model/video";
import {DomSanitizer, SafeHtml, SafeResourceUrl} from "@angular/platform-browser";

@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.css"]
})
export class HomeComponent implements OnInit {



  constructor() { }

  ngOnInit() {
  }


}
