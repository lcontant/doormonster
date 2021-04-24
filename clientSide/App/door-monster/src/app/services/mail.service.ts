import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "./api.service";
import {MailRequest} from "../model/mail-request";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MailService {

  constructor(private httpClient : HttpClient,
              private apiService: ApiService) {

  }

  public sendMail(sender: string, body: string, email) {
      let requestUrl = `${this.apiService.getBaseURL()}/mail/send`;
      let mailRequest: MailRequest = new MailRequest();
      mailRequest.body = body;
      mailRequest.sender = sender;
      mailRequest.email = email;
      return  this.httpClient.post(requestUrl,mailRequest);
  }

  public reportBug(message: string): Observable<string> {
    let requestUrl = `${this.apiService.getBaseURL()}/mail/report`;
    return this.httpClient.post<string>(requestUrl, message)
  }
}
