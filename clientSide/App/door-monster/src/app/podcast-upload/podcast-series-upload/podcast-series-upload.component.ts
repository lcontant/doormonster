import { Component, OnInit } from '@angular/core';
import {Podcast} from "../../model/podcast";
import {PodcastService} from "../../services/podcast.service";
import {ImageService} from "../../services/image.service";

@Component({
  selector: 'app-podcast-series-upload',
  templateUrl: './podcast-series-upload.component.html',
  styleUrls: ['./podcast-series-upload.component.css']
})
export class PodcastSeriesUploadComponent implements OnInit {

  podcast: Podcast;
  thumbnailFile: File;
  currentThumbnail: string;
  errorMessage: string;
  successMessage: string;
  progressMessage: string;
  uploadProgress: number;

  constructor(private podcastService: PodcastService, private imageService: ImageService) { }

  ngOnInit() {
    this.uploadProgress = 0;
    this.successMessage = "";
    this.errorMessage = "";
    this.progressMessage = "";
    this.podcast = new Podcast();
  }

  uploadPodcastSeries() {
    this.progressMessage = "uploading the podcast metadata";
    this.uploadProgress = 0;
    this.podcastService.uploadPodcast(this.podcast).subscribe((response) => {
      this.uploadProgress = 20;
      this.progressMessage = "metadata uploaded. Uploading the thumbnail";
      this.podcastService.uploadSeriesThumbnail(this.thumbnailFile, this.podcast.thumbnailPath).subscribe((response) => {
        this.uploadProgress = 100;
        this.progressMessage = "";
        this.successMessage = "Podcast all uploaded"
      },
        (error) => {
          this.progressMessage = "";
          this.successMessage = "";
          this.errorMessage = "";
      })
    }, (error) => {
      this.progressMessage = "";
      this.successMessage = "";
      this.errorMessage = "There was an error while uploading the podcast " + error;
    });
  }

  onThumbnailChanged($event) {
    this.thumbnailFile = $event.target.files[0];
    this.podcast.thumbnailPath = this.thumbnailFile.name;
    this.setCurrentImage();
  }

  private setCurrentImage() {
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent) => {
      this.currentThumbnail = <string>reader.result;
      this.thumbnailFile = this.imageService.toBlob(this.currentThumbnail);
    };
    reader.readAsDataURL(this.thumbnailFile);
  }

  onSupporterCheckboxClicked() {
    this.podcast.supporterOnly = !this.podcast.supporterOnly;
  }
}
