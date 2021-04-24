import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ApiService} from "./api.service";
import {SessionService} from "./session.service";
import {Observable} from "rxjs";
import {Supporter} from "../model/supporter";
import {Http} from "@angular/http";
import * as Stripe from "stripe";
import ICard = Stripe.cards.ICard;
import ICustomer = Stripe.ephemeralKeys.ICustomer;
import {cards} from "stripe";
import {SupporterWithUser} from "../model/supporter-with-user";

@Injectable({
  providedIn: 'root'
})
export class SupportService {

  constructor(private httpClient: HttpClient, private apiService: ApiService, private sessionService: SessionService) {

  }

  subscribe(token: any, amount: number): Observable<string> {
    const request = `${this.apiService.getBaseURL()}/support/subscribe`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "Amount": amount.toString()
    });
    return this.httpClient.post<string>(request, token, {headers: headers});
  }

  unsubscribe(): Observable<string> {
    const request = `${this.apiService.getBaseURL()}/support/unsubscribe`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.put<string>(request, null, {headers: headers});
  }

  getPeriodEnd(): Observable<Date> {
    const request = `${this.apiService.getBaseURL()}/support/periodEnd`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<Date>(request, {headers: headers});
  }

  renew(): Observable<string> {
    const request = `${this.apiService.getBaseURL()}/support/renew`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.put<string>(request,null,{headers: headers});
  }

  getCurrent(): Observable<Supporter> {
    const request = `${this.apiService.getBaseURL()}/support/current`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<Supporter>(request, {headers: headers});
  }

  deleteCard(cardId: string): Observable<cards.ICard[]> {
    const request = `${this.apiService.getBaseURL()}/support/card/delete`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "cardID": cardId
    });
    return this.httpClient.delete<cards.ICard[]>(request, {headers: headers});
  }

  createCard(cardToken: string): Observable<cards.ICard[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/support/card/create`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "Content-Type": "application/json"
    });
    const token: string = cardToken;
    const body = JSON.stringify(token);
    return this.httpClient.post<cards.ICard[]>(requestUrl, body,{headers: headers});
  }

  getAllCards(): Observable<cards.ICard[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/support/card/list`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "Content-Type": "application/json"
    });
    return this.httpClient.get<cards.ICard[]>(requestUrl, {headers: headers});
  }

  updateCard(cardID: string, updateParams: Map<string, Object>){
    const requestUrl = `${this.apiService.getBaseURL()}/support/card/update`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "cardID": cardID
    });
    const body = JSON.stringify(updateParams);
    return this.httpClient.post<cards.ICard[]>(requestUrl, body, {headers: headers});
  }

  upgradePlan(ammount: number): Observable<Supporter> {
    const requestUrl = `${this.apiService.getBaseURL()}/support/upgrade`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "ammount": ammount.toString()
    });
    return this.httpClient.put<Supporter>(requestUrl, null,{headers: headers});
  }

  list(): Observable<SupporterWithUser[]> {
    const requestUrl = `${this.apiService.getBaseURL()}/support/list`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<SupporterWithUser[]>(requestUrl, {headers: headers});
  }

}
