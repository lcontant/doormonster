import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "./api.service";
import {Observable} from "rxjs/internal/Observable";
import {Friend} from "../model/friend";

@Injectable({
  providedIn: 'root'
})
export class FriendService {

  constructor(private apiService: ApiService
    ,private httpService : HttpClient) {

  }

  public getRandomFriends(): Observable<any> {
    let requestURl = `${this.apiService.getBaseURL()}/friends/random/7`;
    return this.httpService.get(requestURl);
  }
}
