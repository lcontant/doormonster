import { Injectable } from "@angular/core";
import {ApiService} from "./api.service";
import {HttpClient, HttpEvent, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {Video} from "../model/video";
import {Series} from "../model/series";
import {SeriesWithVideos} from "../model/SeriesWithVideos";
import {SessionService} from "./session.service";

@Injectable({
  providedIn: "root"
})
export class VideoService {

  constructor(private apiService: ApiService, private httpClient: HttpClient,private sessionService: SessionService) {

  }

  getVideosForQuery(query: string): Observable<Video[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/search/${query}`;
    return this.httpClient.get<Video[]>(requestUrl);
  }


  getSeries(): Observable<Series[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/series/all`;
    return this.httpClient.get<Series[]>(requestUrl);
  }

  getEpisodesFor(title: string): Observable<Video[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/episodes/${title}`;
    return this.httpClient.get<Video[]>(requestUrl);
  }

  getSeriesById(id: string): Observable<Series> {
    const requestUrl = `${this.apiService.getBaseURL()}/series/${id}`;
    return this.httpClient.get<Series>(requestUrl);
  }

  getByVideoId(id: string): Observable<Video> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/${id}`;
    return this.httpClient.get<Video>(requestUrl);
  }

  addViewToVideo(id: string): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/add/view/${id}`;
    return this.httpClient.put<string>(requestUrl,null);
  }

  getSeriesWithVideos(numberOfEpisodes: Number): Observable<SeriesWithVideos[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/series/episodes`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "numberOfEpisodes": numberOfEpisodes.toString()
    });
    return this.httpClient.get<SeriesWithVideos[]>(requestUrl, {headers: requestHeaders});
  }

  getSeriesNames(): Observable<string[]> {
    const resquestUrl = `${this.apiService.getBaseURL()}/series/names`;
    return this.httpClient.get<string[]>(resquestUrl);
  }

  getCategoriesNames(): Observable<string[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/categories/name`;
    return this.httpClient.get<string[]>(requestUrl);
  }
  uploadVideo(video: Video, seriesId: string[]){
    const requestUrl = `${this.apiService.getBaseURL()}/video/upload`;
    const requestHeader: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "seriesId": seriesId
    });
    return this.httpClient.post(requestUrl, {... video}, {headers: requestHeader})
  }

  createSeries(series: Series): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/series/upload`;
    const requestHeaders: HttpHeaders = new HttpHeaders( {
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.post<string>(requestUrl, {...series}, {headers: requestHeaders});
  }

  uploadSeriesThumbnail(file: File, name: string, seriesTextId: string) {
    const requestUrl = `${this.apiService.getBaseURL()}/series/thumbnail/${seriesTextId}`;
    const formData: FormData = new FormData();
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    formData.append("file", file, name);
    return this.httpClient.post<string>(requestUrl, formData, {headers: headers});
  }

  uploadThumbnail(file: File, name: string, prePath: string) {
    const requestUrl = `${this.apiService.getBaseURL()}/video/thumbnail`;
    const formData:  FormData = new FormData();
    const requestHeader :HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    formData.append("file", file , name);
    return this.httpClient.post(requestUrl, formData,{headers: requestHeader});
  }

  uploadVideoFile(file: File, name: string) {
    const requestUrl = `${this.apiService.getBaseURL()}/video/upload/file`;
    const formData: FormData = new FormData();
    const requestHeader: HttpHeaders = new HttpHeaders({
        "SessionId": this.sessionService.getSessionId()
    });
    formData.append("file", file, name);
    return this.httpClient.post(requestUrl, formData, {headers: requestHeader, reportProgress: true});
  }

  getAllVideos(): Observable<Video[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/all`;
    const requestHeader: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<Video[]>(requestUrl,{headers: requestHeader});
  }

  updateVideo(video: Video): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/update`;
    const requestHeader: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.put<string>(requestUrl,video,{headers: requestHeader});
  }

  deleteVideo(id: number): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/delete/${id}`;
    const requestHeaders: HttpHeaders= new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.delete<string>(requestUrl,{headers: requestHeaders});
  }

  getSeriesForVideo(id: number): Observable<Series> {
    const requetUrl= `${this.apiService.getBaseURL()}/series/name/${id}`;
    return this.httpClient.get<Series>(requetUrl);
  }

  getSeriesByPublishDate(): Observable<Series[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/series/all/ordered`;
    return this.httpClient.get<Series[]>(requestUrl);
  }

  getLatestVideos(numberOfVideos: number): Observable<Video[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/video/latest/${numberOfVideos}`;
    return this.httpClient.get<Video[]>(requestUrl);
  }


}
