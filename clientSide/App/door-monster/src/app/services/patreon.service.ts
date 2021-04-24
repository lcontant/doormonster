import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {patreonPostResponse} from "../model/patreonPostResponse";
import {UserService} from "./user.service";
import {SessionService} from "./session.service";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PatreonService {

  constructor(private apiService: ApiService,private httpClient: HttpClient, private sessionService: SessionService) {

  }


  getPosts() {
    let requestUrl = `${this.apiService.getBaseURL()}/patreon/posts`;
    const requestHeaders:HttpHeaders = new HttpHeaders( {
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<patreonPostResponse>(requestUrl, {headers: requestHeaders});
  }

  currentUserIsPatron() : Observable<boolean> {
    let requestUrl = `patreon/isPatron`;
    const requestHeaders:HttpHeaders = new HttpHeaders( {
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<boolean>(this.apiService.getBaseURL() + "/" + requestUrl,{headers: requestHeaders});
  }

  acquireTokenForUser(code: string): Observable<string> {
    let requestUrl = `${this.apiService.getBaseURL()}/patreon/registerToken`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "code": code
    });
    return this.httpClient.post<string>(requestUrl,null,{headers: requestHeaders});
  }

}
