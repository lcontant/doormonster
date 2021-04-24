import {Component, OnChanges, OnInit} from '@angular/core';
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {Video} from "../model/video";
import {VideoService} from "../services/video.service";
import {map} from "rxjs/operators";
import {Util} from "../services/util";

@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.css']
})
export class SearchResultsComponent implements OnInit {

  searchQuery: string;
  videos: Video[];
  constructor(private route: ActivatedRoute,
              private videoService: VideoService,
              private router: Router) { }

  ngOnInit() {
      this.setupData();
      this.subscribeToRouter();
  }

  subscribeToRouter() {
     this.route.params.subscribe(params => {
        this.setupData();
     });
  }

  setupData() {
    this.searchQuery = this.route.snapshot.paramMap.get('query');
    this.getVideos();
  }
  sanitizePath(path: string): string {
    return Util.removeSpecialStrings(path);
  }

  private getVideos() {
    this.videoService.getVideosForQuery(this.searchQuery)
      .subscribe(response => {
        this.videos = response;
    });
  }

  scroll_back_to_top() {
    let scrollOptions: ScrollToOptions = {
      left: 0,
      top: 0,
      behavior: 'smooth'
    };
    window.scrollTo(scrollOptions)
  }

}
