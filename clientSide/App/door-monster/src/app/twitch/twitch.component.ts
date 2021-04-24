import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-twitch',
  templateUrl: './twitch.component.html',
  styleUrls: ['./twitch.component.css']
})
export class TwitchComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    // @ts-ignore
    new Twitch.Embed("twitch-embed", {
      width: "100%",
      height: "100%",
      channel: "doormonstertv"
    });
  }

}
