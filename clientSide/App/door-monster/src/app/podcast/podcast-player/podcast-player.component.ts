import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AudioPlayerService} from "../../services/audio-player.service";
import {Observer} from "rxjs";

@Component({
  selector: 'app-podcast-player',
  templateUrl: './podcast-player.component.html',
  styleUrls: ['./podcast-player.component.css']
})
export class PodcastPlayerComponent implements OnInit,Observer<string>, AfterViewInit {

  @ViewChild("audioPlayer")
  audioPlayerRef: ElementRef;

  closed: boolean;
  audioPlayer: HTMLAudioElement;
  audioSource: string;
  duration: number;
  seekbarTime: number;

  constructor(private audioPlayerService: AudioPlayerService) { }

  ngOnInit() {
    this.audioPlayerService.subscribe(this);
  }

  ngAfterViewInit(): void {
    this.initAudioPlayer();
  }

  initAudioPlayer() {
    if (this.audioPlayerRef) {
      this.audioPlayer = this.audioPlayerRef.nativeElement;
    }
  }

  next(value: string) {
    this.audioSource = value;
    this.initAudioPlayer();
    this.audioPlayer.src = value;
  }

  close() {
    this.audioSource = undefined;
  }


  complete() {

  }

  error(err: any) {

  }



}

