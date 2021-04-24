import { Component, OnInit } from '@angular/core';
import {Podcast} from "../model/podcast";
import {PodcastService} from "../services/podcast.service";
import {Observable} from "rxjs/internal/Observable";
import {PodcastEpisode} from "../model/podcast-episode";
import {SupportService} from "../services/support.service";
import {UserService} from "../services/user.service";
import {User} from "../model/user";
import {isSuccess} from "@angular/http/src/http_utils";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-podcast',
  templateUrl: './podcast.component.html',
  styleUrls: ['./podcast.component.css']
})

export class PodcastComponent implements OnInit {

  podcasts : Podcast[];
  podcastEpisodes: PodcastEpisode[];
  activePodcastIndex: number;
  activePodcast: Podcast;
  currentUser: User;

  constructor(private podcastService: PodcastService, private supporterService: SupportService, private userService: UserService, private notificationService: NotificationService) { }

  ngOnInit() {
    this.podcasts = [];
    this.activePodcastIndex = -1;
    this.determinePodcastListing();
  }

  determinePodcastListing() {
    this.userService.getUser().subscribe(success => {
         this.currentUser = success;
         if (this.currentUser.patreonContribution >= 500) {
           this.getAllPodcastsIncludingSupporters();
         } else {
           this.supporterService.getCurrent().subscribe(success => {
                if (success && success.ammount >= 500) {
                    this.getAllPodcastsIncludingSupporters();
                } else {
                  this.getAllPodcasts();
                }
           }, error => {
              this.getAllPodcasts();
           });
         }
    }, error => {
      this.getAllPodcasts();
    })
  }

  getAllPodcastsIncludingSupporters() {
    this.podcastService.getPodcastForSupporters().subscribe(success => {
      this.podcasts = success;
    }, error => {
      this.notificationService.createErrorNotification("Error encountered, couldn't load the podcasts");
    })
  }

  getAllPodcasts() {
    this.podcastService.getPodcasts().subscribe((response) => {
      this.podcasts = response;
    });
  }

}
