import { Injectable, EventEmitter } from '@angular/core';
import { Observer } from 'rxjs';
import { CallbackFunction } from '@vimeo/player';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {

  public isLoading: boolean;


  constructor() { 
    this.isLoading = false;
  }

  startLoading(){
    this.isLoading = true;
  }

  stopLoading() {
    this.isLoading = false;
  }
}
