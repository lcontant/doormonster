import {AfterViewChecked, AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {Video} from "../../model/video";
import Player from "@vimeo/player";
import {VideoService} from "../../services/video.service";
import {ActivatedRoute} from "@angular/router";
var Plyr = require("plyr");
@Component({
  selector: 'app-video-player',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.css']
})
export class VideoPlayerComponent implements OnInit, OnChanges, AfterViewInit, AfterViewChecked {

  @Input() video: Video;
  @Input() isEmbeded: boolean;
  @Output() videoEndend: EventEmitter<boolean> = new EventEmitter();
  @Input() autoPlayEnabled: boolean;

  nativePlayerControls = [
    'restart', // Restart playback
    'rewind', // Rewind by the seek time (default 10 seconds)
    'play', // Play/pause playback
    'fast-forward', // Fast forward by the seek time (default 10 seconds)
    'progress', // The progress bar and scrubber for playback and buffering
    'current-time', // The current time of playback
    'duration', // The full duration of the media
    'mute', // Toggle mute
    'volume', // Volume control
    'captions', // Toggle captions
    'settings', // Settings menu
    'pip', // Picture-in-picture (currently Safari only)
    'airplay', // Airplay (currently Safari only)
    'fullscreen', // Toggle fullscreen
  ];
  videoUrl: string;
  nativeVideoUrl: string;
  posterUrl: string;
  previousVideWasNative: boolean;
  player: Player;
  nativePlayer;
  hasViewed: boolean;


  constructor(private sanitizer: DomSanitizer, private route: ActivatedRoute, private videoService: VideoService) {
    this.hasViewed = false;
  }

  ngOnInit() {
    this.route.params.subscribe(() => {
      this.videoUrl = "https://player.vimeo.com/video/" + this.video.videoID;
      this.posterUrl = "https://s3.amazonaws.com/doormonster/assets/images/videos/" + this.video.videoThumbnail;
      if (this.video.videoFileLink) {
        this.previousVideWasNative = true;
        this.nativeVideoUrl = "https://s3.amazonaws.com/doormonster/assets/videos/" + this.video.videoFileLink;
      }
      this.hasViewed = false;
    });
  }

  ngAfterViewInit(): void {
    this.subscribeToNativeVideoPlayer();
  }

  ngAfterViewChecked(): void {
  }


  addViewToCurrentVideo() {
    if (!this.hasViewed) {
      this.videoService.addViewToVideo(this.video.videoID).subscribe(() => {
        this.hasViewed = true;
      });
    }
  }

  subscribeToNativeVideoPlayer() {
    this.nativePlayer = new Plyr('#player', {controls: this.nativePlayerControls, hideControls: true});
    if (this.nativePlayer) {
      this.subscribeToPlayerEvent();
      console.log(this.nativePlayer);
    }
  }

  subscribeToPlayerEvent() {
      this.nativePlayer.on('play',  () => {
        this.addViewToCurrentVideo();
      });
      this.nativePlayer.on('pause', (event) => {
      });
      this.nativePlayer.on('mouseover', (event) => {
        this.turnOnControls();
      });
      this.nativePlayer.on('mouseout', (event) => {
        this.turnOffControls();
      });
      this.nativePlayer.on('ended',  () => {
        this.videoEndend.emit(true);
      })
  }
  turnOnControls() {
    if (this.nativePlayer.paused) {
      document.querySelector('.plyr__controls').setAttribute('style', 'display: flex;');
    }
  }

  turnOffControls() {
    if (this.nativePlayer.paused) {
      document.querySelector('.plyr__controls').setAttribute('style', 'display: none;');
    }
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes.video) {
      if (!! this.previousVideWasNative != !! this.video.videoFileLink) {
          this.previousVideWasNative = !this.previousVideWasNative;
      }
      if (this.nativePlayer) {
        this.updatePlayerSources();
        this.nativePlayer.autoplay = this.autoPlayEnabled;
      }
    }
  }

  updatePlayerSources() {
    if (!this.video.videoFileLink) {
      this.nativePlayer.source = {
        type: 'video',
        sources: [
          {
            src: this.video.videoID,
            provider: 'vimeo',
          },
        ],
      };
    } else {

      this.nativeVideoUrl = "https://s3.amazonaws.com/doormonster/assets/videos/" + this.video.videoFileLink;
      this.posterUrl = "https://s3.amazonaws.com/doormonster/assets/images/videos/" + this.video.videoThumbnail;
      this.nativePlayer.source = {
        type: 'video',
        sources: [
          {
            src: this.nativeVideoUrl,
            type: 'video/mp4',
          },
        ],
        poster: this.posterUrl,
      };
    }
  }

}
