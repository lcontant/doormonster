import {Injectable} from '@angular/core';
import {SessionService} from "./session.service";
import {ApiService} from "./api.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Feedback} from "../model/feedback";
import {FeedbackWithUser} from "../model/feedback-with-user";

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

  constructor(private httpClient: HttpClient,
              private apiService: ApiService,
              private sessionService: SessionService) {

  }

  public sendCreatorSuggestion(suggestion: string): Observable<string> {
    const requestUrl: string = `${this.apiService.getBaseURL()}/feedback/creator`;
    const requestHeader: HttpHeaders = new HttpHeaders({
      "sessionId": this.sessionService.getSessionId()
    });
    let feedback: Feedback = new Feedback();
    feedback.content = suggestion;
    return this.httpClient.post<string>(requestUrl,feedback, {headers: requestHeader});
  }

  public sendUserFeedback(content: string): Observable<boolean> {
    const requestUrl: string = `${this.apiService.getBaseURL()}/feedback/user`;
    const requestHeader: HttpHeaders = new HttpHeaders({
      "sessionId": this.sessionService.getSessionId()
    });
    let feedback: Feedback = new Feedback();
    feedback.content = content;
    return this.httpClient.post<boolean>(requestUrl, feedback, {headers: requestHeader});
  }

  public getGetAllFeedback(): Observable<FeedbackWithUser[]>{
    const requestUrl: string = `${this.apiService.getBaseURL()}/feedback`;
    const requestHeader: HttpHeaders = new HttpHeaders({
      "sessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<FeedbackWithUser[]>(requestUrl, {headers: requestHeader});
  }
}
