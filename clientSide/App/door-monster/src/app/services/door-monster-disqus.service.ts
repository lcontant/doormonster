import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Video} from "../model/video";

@Injectable({
  providedIn: 'root'
})
export class DoorMonsterDisqusService {
  headers: HttpHeaders = new HttpHeaders();
  constructor(private httpClient: HttpClient) {

  }

  getCommentCountForVideo(video:Video) {
    this.headers.set("Origin" ,"http://doormonster.tv");
   return this.httpClient.get(
     "https://doormonster.disqus.com/count-data.js?2=http://doormonster.tv/video/" + video.videoID
     ,{headers: this.headers} );
  }
}
