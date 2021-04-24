import { Injectable } from '@angular/core';
import {Observer} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AudioPlayerService {

  observers: Observer<string>[];

  constructor() {
    this.observers = [];
  }

  subscribe(observer: Observer<string>) {
    this.observers.push(observer);
  }

  setCurrentAudioSource(audioSource: string) {
    this.observers.forEach(observer => {
      observer.next(audioSource);
    });
  }

}
