import { Component, OnInit } from '@angular/core';
import {PodcastEpisode} from "../model/podcast-episode";
import {ImageService} from "../services/image.service";
import {Podcast} from "../model/podcast";
import {PodcastService} from "../services/podcast.service";
import {NotificationService} from "../services/notification.service";
import {ApiService} from "../services/api.service";

@Component({
  selector: 'app-podcast-upload',
  templateUrl: './podcast-upload.component.html',
  styleUrls: ['./podcast-upload.component.css']
})
export class PodcastUploadComponent implements OnInit {
  thumbnailFile: File;
  currentThumbnail: string;
  podcastEpisode: PodcastEpisode;
  podcasts: Podcast[];
  audioFile: File;
  currentAudioFileName: string;
  currentThumbnailFileName: string;
  audioFilePrePath: string;
  thumbnailPrePath: string;
  errorMessage: string;
  successMessage: string;
  progressMessage: string;
  uploadProgress: number;

  constructor(private podcastService: PodcastService,private imageService: ImageService, private notificationService: NotificationService, private apiService: ApiService) { }

  ngOnInit() {
    this.podcastEpisode = new PodcastEpisode();
    this.podcasts = [];
    this.getAllPodcastSeries();
    this.uploadProgress = 0;
  }

  onThumbnailChanged($event) {
    this.thumbnailFile = $event.target.files[0];
    this.currentThumbnailFileName = this.thumbnailFile.name;
    this.podcastEpisode.episodeThumbnail = "/assets/podcasts/" + this.thumbnailFile.name;
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

  private getAllPodcastSeries(){
    this.podcastService.getAllSeriesForUploading().subscribe(succes => {
      this.podcasts = succes;
      this.podcastEpisode.podcast = this.podcasts[0].podcastTitle;
      this.onSeriesChange();
    }, error => {
      this.notificationService.createErrorNotification("Error while loading the podcast list");
    });
  }

  onAudioChanged($event) {
    this.audioFile = $event.target.files[0];
    this.currentAudioFileName = this.audioFile.name;
  }

  uploadPodcast() {
    if (this.podcastEpisode.episodePublishDate.indexOf("T00:00:00") == -1) {
      this.podcastEpisode.episodePublishDate += "T00:00:00";
    }
    this.progressMessage = "";
    this.errorMessage = "";
    this.successMessage = "";
    this.uploadProgress = 0;
    this.podcastEpisode.episodeLink = this.apiService.BASE_STORAGE_URL + "/assets/podcasts/" + this.audioFile.name;
    this.podcastEpisode.episodeThumbnail = this.currentThumbnailFileName;
    this.podcastService.uploadEpisode(this.podcastEpisode).subscribe(success => {
      this.podcastEpisode = success;
      this.progressMessage = "Uploaded podcast metadata";
      this.uploadProgress = 10;
      this.podcastService.uploadAudio(this.audioFile, this.audioFile.name, this.podcastEpisode.episodeID).subscribe(
        success => {
          this.progressMessage ="Audio file uploaded uploading thumbnail";
          this.uploadProgress = 20;
          this.podcastService.uploadThumbnail(this.thumbnailFile, this.podcastEpisode.episodeThumbnail, this.podcastEpisode.episodeID)
            .subscribe(success => {
              this.successMessage = "Full podcast upload completed";
              this.progressMessage = "";
              this.uploadProgress = 100;
            }, error => {
              this.progressMessage = "";
              this.errorMessage = "There was an error while uploading the audio file";
            });
        }
        , error => {
          this.progressMessage = "";
          this.errorMessage = "There was an error while uploading the thumbnail file";
        });
    }, error => {
      this.progressMessage = "";
      this.errorMessage = "There was an error while uploading the metadata";
    });
  }

  onSeriesChange() {
    this.podcastService.getEpisodeCount(this.podcastEpisode.podcast).subscribe(response => {
        this.podcastEpisode.episodeNum = response + 1;
    }, error => {

    });
  }
}
