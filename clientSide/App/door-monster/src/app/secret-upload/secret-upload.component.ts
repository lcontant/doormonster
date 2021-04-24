import { Component, OnInit } from '@angular/core';
import {VideoSeriesUploadComponent} from "../video-upload/video-series-upload/video-series-upload.component";
import {VideoService} from "../services/video.service";

@Component({
  selector: 'app-secret-upload',
  templateUrl: './secret-upload.component.html',
  styleUrls: ['./secret-upload.component.css']
})
export class SecretUploadComponent implements OnInit {
  private videoFile: File;

  constructor(private videoService: VideoService) { }

  ngOnInit() {
  }

  onVideoFileChanged($event) {
    this.videoFile = $event.target.files[0];
  }

  upload() {
    this.videoService.uploadVideoFile(this.videoFile, this.videoFile.name).subscribe(response => {
    }, error => {
    });
  }
}
