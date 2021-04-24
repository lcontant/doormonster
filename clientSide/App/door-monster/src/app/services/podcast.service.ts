import { Injectable } from "@angular/core";
import {ApiService} from "./api.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Podcast} from "../model/podcast";
import {Observable} from "rxjs/internal/Observable";
import {PodcastEpisode} from "../model/podcast-episode";
import {SessionService} from "./session.service";

@Injectable({
  providedIn: "root"
})
export class PodcastService {

  constructor(private apiService: ApiService
              , private httpClient: HttpClient
              , private sessionService: SessionService) {

  }


  getPodcasts(): Observable<Podcast[]> {
    const requestURL = `${this.apiService.getBaseURL()}/podcasts`;
    return this.httpClient.get<Podcast[]>(requestURL);
  }

  getPodcast(id: number): Observable<Podcast> {
    const requestURL = `${this.apiService.getBaseURL()}/podcasts/${id}`;
    return this.httpClient.get<Podcast>(requestURL);
  }

  getPodcastEpisodes(): Observable<PodcastEpisode[]> {
    const requestURL = `${this.apiService.getBaseURL()}/podcastepisodes`;
    return this.httpClient.get<PodcastEpisode[]>(requestURL);
  }

  getPodcastEpisode(id: number): Observable<PodcastEpisode> {
    const requestURL = `${this.apiService.getBaseURL()}/podcastepisodes/${id}`;
    return this.httpClient.get<PodcastEpisode>(requestURL);
  }

  getEpisodeCount(title: string): Observable<number> {
    const requestURL = `${this.apiService.getBaseURL()}/podcastepisodes/count/${title}`;
    return this.httpClient.get<number>(requestURL);
  }

  getPodcastByTitle(title: string): Observable<Podcast> {
    const requestURL = `${this.apiService.getBaseURL()}/podcasts/${title}`;
    return this.httpClient.get<Podcast>(requestURL);
  }

  getEpisodes(title: string): Observable<PodcastEpisode[]> {
    const requestURL = `${this.apiService.getBaseURL()}/podcastepisodes/episodes/${title}`;
    return this.httpClient.get<PodcastEpisode[]>(requestURL);
  }

  getPodcastForSupporters(): Observable<Podcast[]> {
    const reqquestURL = `${this.apiService.getBaseURL()}/podcasts/supporter`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<Podcast[]>(reqquestURL, {headers: headers});
  }

  uploadPodcast(podcast: Podcast): Observable<any> {
   const requestURL = `${this.apiService.getBaseURL()}/podcasts/upload`;
   const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
   });
   return this.httpClient.post<string>(requestURL, {... podcast}, {headers: headers});
  }

  uploadEpisode(episode: PodcastEpisode): Observable<PodcastEpisode> {
    const requestURL = `${this.apiService.getBaseURL()}/podcastepisodes/upload`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.post<PodcastEpisode>(requestURL, {... episode}, {headers: headers});
  }

  uploadAudio(file: File, name: string, episodeId: number) {
    const requestUrl = `${this.apiService.getBaseURL()}/podcastepisodes/upload/audio/${episodeId}`;
    const formData:  FormData = new FormData();
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    formData.append("file", file, name);
    return this.httpClient.post(requestUrl, formData, {headers: headers});
  }

  uploadThumbnail(file: File, name: string,  episodeId: number) {
    const requestUrl = `${this.apiService.getBaseURL()}/podcastepisodes/upload/thumbnail/${episodeId}`;
    const formData: FormData = new FormData();
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()});
    formData.append("file", file, name);
    return this.httpClient.post(requestUrl, formData, {headers: headers, reportProgress: true});
  }

  uploadSeriesThumbnail(file: File, name: string) {
    const requestUrl = `${this.apiService.getBaseURL()}/podcasts/uploadThumbnail`;
    const formData: FormData = new FormData();
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    formData.append("file", file, name);
    return this.httpClient.post(requestUrl, formData, {headers: headers});
  }

  getAllSeriesForUploading() : Observable<Podcast[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/podcasts/all`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<Podcast[]>(requestUrl, {headers: headers});
  }
}
