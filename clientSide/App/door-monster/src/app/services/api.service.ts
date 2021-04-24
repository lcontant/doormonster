import { Injectable } from "@angular/core";
import {HttpClient} from "@angular/common/http";


@Injectable({
  providedIn: "root"
})
export class ApiService {
   BASE_STORAGE_URL: string = "https://s3.amazonaws.com/doormonster";
  constructor() {
  }

  public getBaseURL(): string {
    let hostname: string = "";
    if (location.hostname != "localhost" && location.hostname.indexOf("www") == -1){
       hostname = "www." + location.hostname;
    } else {
      hostname = location.hostname;
    }
    return `https://${hostname}:8080`;
  }

  public getClientBaseURL(): string{

    let hostname: string = "";
    if (location.hostname != "localhost" && location.hostname.indexOf("www") == -1){
      hostname = "www." + location.hostname;
    } else {
      hostname = location.hostname;
    }

    if (hostname.indexOf("localhost") != -1) {
      hostname += ":4200";
    }
    return `https://${hostname}`;
  }
}
