import {AfterContentInit, AfterViewInit, Component, ElementRef, OnInit, ViewChild} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {VideoService} from "../services/video.service";
import {Video} from "../model/video";
import {DomSanitizer, SafeHtml, SafeResourceUrl} from "@angular/platform-browser";
import Player from "@vimeo/player";
import {Series} from "../model/series";


@Component({
  selector: "app-video",
  templateUrl: "./video.component.html",
  styleUrls: ["./video.component.css"]
})
export class VideoComponent implements OnInit {
  @ViewChild("List") public List: ElementRef<any>;

  videoId: string;
  videoUrl: SafeResourceUrl;
  video: Video;
  series: Series;
  player: Player;
  relatedVideos: Video[];
  hasViewed = false;
  autoPlayEnabled = false;
  playListReversed = false;

  constructor(private route: ActivatedRoute,
              private videoService: VideoService,
              private sanitizer: DomSanitizer,
              private router: Router) {
  }

  ngOnInit() {
    this.setupData();
    this.route.params.subscribe(params => {
      this.setupData();
    });
  }


  setupData() {
    this.videoId = this.route.snapshot.paramMap.get("id");
    this.hasViewed = false;
    this.videoService.getByVideoId(this.videoId).subscribe(response => {
      this.video = response;
      this.videoService.getSeriesForVideo(this.video.id).subscribe(seriesResponse => {
        this.series = seriesResponse;
        this.videoUrl = this.sanitizer.bypassSecurityTrustResourceUrl("https://player.vimeo.com/video/" + this.video.videoID);
        this.videoService.getEpisodesFor(this.series.title).subscribe(response => {
          this.relatedVideos = response;
          if (this.playListReversed) {
            this.relatedVideos.reverse();
          }
        });
      });
    });
  }

  clickRight() {
    const element = this.List.nativeElement;
    const startLeft = element.scrollLeft;
    this.List.nativeElement.scrollTo({
      left: startLeft + 912,
      behavior: "smooth"
    });

  }

  clickLeft() {
    const element = this.List.nativeElement;
    const startLeft = element.scrollLeft;
    this.List.nativeElement.scrollTo({
      left: startLeft - 912,
      behavior: "smooth"
    });

  }

  toggleAutoPlay() {
    this.autoPlayEnabled = !this.autoPlayEnabled;
  }

  reversePlaylist() {
    this.playListReversed = !this.playListReversed;
    this.relatedVideos.reverse();
  }

  async onVideoEnded() {
    if (this.autoPlayEnabled) {
      let currentVideoIndex: number = this.relatedVideos.findIndex(vid => vid.videoID == this.video.videoID);
      if (currentVideoIndex == this.relatedVideos.length - 1) {
        currentVideoIndex = -1;
      }
      const nextVideo = this.relatedVideos[currentVideoIndex + 1];
      await this.router.navigateByUrl(`/video/${nextVideo.videoID}`);
    }
  }

}
