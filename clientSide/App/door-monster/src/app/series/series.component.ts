import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Route} from "@angular/router";
import {VideoService} from "../services/video.service";
import {Video} from "../model/video";
import {Series} from "../model/series";
import {Util} from "../services/util";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'app-series',
  templateUrl: './series.component.html',
  styleUrls: ['./series.component.css']
})
export class SeriesComponent implements OnInit {

  seriesId: string;
  videos: Video[];
  series: Series;
  categories: string[];
  expandedStates: boolean[];

  constructor(private route: ActivatedRoute,
              private videoService: VideoService ,
              private sanitizer:DomSanitizer) { }

  ngOnInit() {
     this.seriesId =  this.route.snapshot.paramMap.get('seriesId');
     this.videoService.getSeriesById(String(this.seriesId)).subscribe(response => {
        this.series = response;
        this.videoService.getEpisodesFor(this.series.title).subscribe(response => {
            this.videos = response;
            this.getCategories(this.videos);
        });
     });
  }

  getCategories(videos: Video[]) {
    this.categories = [];
    for (let video of videos) {
          if (this.categories.lastIndexOf(video.videoCategory) == -1){
            this.categories.push(video.videoCategory);
          }
    }
    this.expandedStates = new Array(this.categories.length);

  }

  isLimited(index: number) {
      return this.expandedStates[index];
  }

  toggleLimited(index: number) {
      this.expandedStates[index] = !this.expandedStates[index];
  }

  sanitizePath(path: string): string {
    return Util.removeSpecialStrings(path);
  }

  shortenTitle(title: string) {
    if (title.length > 30) {
      return title.substr(0,30) + "...";
    } else {
      return title;
    }
  }

  getComments(videoId: string) {
    return this.sanitizer.bypassSecurityTrustHtml("<span class=\"disqus-comment-count\" data-disqus-url=\"http://doormonster.tv/videos/" + videoId + "\" style=\"font-style:italic;\">0 Comments</span>");
  }

  getVideosForCategorie(category: string) : Video[]{
    let categoriesVideo: Video[] =[];
    for (let video of this.videos) {
        if (video.videoCategory == category){
          categoriesVideo.push(video);
        }
    }
    return categoriesVideo;
  }

}
