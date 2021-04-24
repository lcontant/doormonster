import {Component, Input, OnInit} from "@angular/core";
import {VideoService} from "../services/video.service";
import {DomSanitizer} from "@angular/platform-browser";
import {Util} from "../services/util";
import {SeriesWithVideos} from "../model/SeriesWithVideos";
import {LoadingService} from "../services/loading.service";
import {Series} from "../model/series";
import {Video} from "../model/video";
import {Comment} from "../model/comment";
import {CommentService} from "../services/comment.service";


@Component({
  selector: "app-videos",
  templateUrl: "./videos.component.html",
  styleUrls: ["./videos.component.css"]
})

export class VideosComponent implements OnInit {

  @Input() videoPerSeries: number;
  @Input() numberOfSeries: number;

  series: Series[];
  videos: Video[];
  comments: Comment[];
  videoSeries: SeriesWithVideos[];

  constructor(private videoService: VideoService
              , private sanitizer: DomSanitizer
              , public loadingService: LoadingService
              , public commentService: CommentService) { }


  ngOnInit() {
    if (!this.videoPerSeries) {
      this.videoPerSeries = 4;
    }
    if (!this.numberOfSeries) { this.numberOfSeries = 10; }
    this.getAllSeries();
  }

  shortenTitle(title: string) {
    if (title.length > 24) {
        return title.substr(0, 24) + "...";
    } else {
      return title;
    }
  }

  sanitizePath(path: string): string {
      return Util.removeSpecialStrings(path);
  }
// TODO: order series by latest videos publish date
  private getAllSeries() {
    this.loadingService.startLoading();
    this.videoService.getSeriesByPublishDate().subscribe(response => {
      this.getLatestVideos();
      this.series = response;
      this.series = this.series.reverse();
    });
  }

  private getVideos() {
    this.videoService.getSeriesWithVideos(this.videoPerSeries).subscribe(response => {
        this.videoSeries = response;
      this.loadingService.stopLoading();
    });
  }

  private getLatestVideos() {
    this.videoService.getLatestVideos(7).subscribe(response => {
      this.videos = response;
      this.getComments();
    });
  }

  private getComments() {
    this.commentService.getTopCommentsOfTheWeek().subscribe(response => {
        this.comments = response;
        this.getVideos();
    });
  }

}
