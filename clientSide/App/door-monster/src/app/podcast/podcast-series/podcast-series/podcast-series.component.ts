import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PodcastService} from "../../../services/podcast.service";
import {PodcastEpisode} from "../../../model/podcast-episode";
import {NotificationService} from "../../../services/notification.service";
import {Podcast} from "../../../model/podcast";
import {AudioPlayerService} from "../../../services/audio-player.service";

@Component({
  selector: 'app-podcast-series',
  templateUrl: './podcast-series.component.html',
  styleUrls: ['./podcast-series.component.css']
})
export class PodcastSeriesComponent implements OnInit {

  seriesId: string;
  episodes: PodcastEpisode[];
  podcast: Podcast;
  currentActiveEpisodeId: number;
  constructor(private route: ActivatedRoute
              , private podcastService: PodcastService
              , private notificationService: NotificationService
              , private audioPlayerService: AudioPlayerService) { }

  ngOnInit() {
    this.seriesId = this.route.snapshot.paramMap.get('seriesId');
    this.currentActiveEpisodeId = undefined;
    this.podcastService.getPodcastByTitle(this.seriesId).subscribe(success => {
        this.podcast = success;
    }, error => {
      this.notificationService.createErrorNotification("Error while loading the podcast");
    });
    this.podcastService.getEpisodes(this.seriesId).subscribe(success => {
      this.episodes = success;
    }, error => {
      this.notificationService.createErrorNotification("Error while loading the episodes");
    })
  }

  choosePodcast(podcastEpisode: PodcastEpisode) {
    this.audioPlayerService.setCurrentAudioSource(podcastEpisode.episodeLink);
  }

  showComments(episodeId: number) {
    if (!this.isShowingComments(episodeId)) {
      this.currentActiveEpisodeId = episodeId;
    } else {
      this.currentActiveEpisodeId = undefined;
    }
  }

  isShowingComments(episodeId: number) {
    return this.currentActiveEpisodeId == episodeId;
  }
}
