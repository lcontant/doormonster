import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class BlogService {

  constructor(private apiService: ApiService
              ,private httpService : HttpClient) {

  }

}
