import {Component, OnInit} from '@angular/core';
import {Video} from "../model/video";
import {VideoService} from "../services/video.service";
import {ImageService} from "../services/image.service";
import {Series} from "../model/series";
import {HttpEvent, HttpEventType} from "@angular/common/http";
import {catchError, last, map, tap} from "rxjs/operators";

@Component({
  selector: 'app-video-upload',
  templateUrl: './video-upload.component.html',
  styleUrls: ['./video-upload.component.css']
})
export class VideoUploadComponent implements OnInit {

  thumbnailFile: File;
  videoFile: File;
  video: Video;
  seriesList: Series[];
  chosensSeries: Series[];
  seriesId: number;
  uploadProgress: number;
  categoriesNames: string[];
  prePath: string;
  currentVideoFile: string;
  currentThumbnail: string;
  videoPrePath: string;
  errorMessage: string;
  successMessage: string;
  progressMessage: string;

  constructor(private videoService: VideoService, private imageService: ImageService) {

  }

  ngOnInit() {
    this.uploadProgress = 0;
    this.video = new Video();
    this.chosensSeries = [];
    this.getSeriesNames();
    this.getCategoriesNames();
  }

  private getSeriesNames() {
    this.videoService.getSeries().subscribe(seriesList => {
      this.seriesList = seriesList;
    })
  }

  private getCategoriesNames() {
    this.videoService.getCategoriesNames().subscribe(names => {
      this.categoriesNames = names;
    })
  }

  onThumbnailChanged($event) {
    this.thumbnailFile = $event.target.files[0];
    this.video.videoThumbnail = this.thumbnailFile.name;
    this.setCurrentImage();
  }

  onVideoFileChanged($event) {
    this.videoFile = $event.target.files[0];
    this.video.videoFileLink = this.videoFile.name;
  }


  private setCurrentImage() {
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent) => {
      this.currentThumbnail = <string>reader.result;
      this.thumbnailFile = this.imageService.toBlob(this.currentThumbnail);
    };
    reader.readAsDataURL(this.thumbnailFile);
  }

  uploadVideoData() {
    if (this.video.videoPublishDate.indexOf("T00:00:00") == -1) {
      this.video.videoPublishDate += "T00:00:00";
    }
    if (this.prePath && this.video.videoThumbnail.indexOf(this.prePath) == -1) {
      this.video.videoThumbnail = this.prePath + "/" + this.video.videoThumbnail;
    }
    this.successMessage = "";
    this.errorMessage = "";
    let series = [];
    for (let seriesIt of this.chosensSeries) {
        series.push(seriesIt.id);
    }
    this.progressMessage = "Starting to upload the video";
    this.uploadProgress = 10;
    this.videoService.uploadVideo(this.video, series).subscribe(
      response => {
        this.progressMessage = "Uploading the thumbnail";
        this.uploadProgress = 20;
        this.videoService.uploadThumbnail(this.thumbnailFile, this.video.videoThumbnail, this.prePath).subscribe(
          response => {
            this.uploadProgress = 40;
            this.successMessage = "The video thumbnail was uploaded";
          }, error => {
            this.errorMessage = "There was a problem uploading the thumbnail send it to Louis on discord"
          });
        if (this.video.videoFileLink) {
          this.progressMessage = "Starting to upload the video file";
          this.videoService.uploadVideoFile(this.videoFile, this.video.videoFileLink).subscribe(response => {
            this.uploadProgress = 100;
            this.progressMessage = null;
            this.successMessage = "The video was uploaded";
          }, error => {
            this.errorMessage = "There was a problem uploading the video file tell Louis and send him a link to the file;";
          });
        }
      },
      error => {
        this.errorMessage = "There was a problem uploading the videos, we didn't even save it in the DB";
      });
  }


  handleUploadEvents(event: HttpEvent<any>, file: File) {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.uploadProgress = Math.round(100 * event.loaded / event.total);
        break;
      case HttpEventType.Response:
        this.successMessage = "The videos was uploaded";
        break;
    }
  }

  addSeries() {
    let series: Series = this.seriesList.find((value) => value.id == this.seriesId);
    let preExistingIndex : number= this.chosensSeries.findIndex((value) => value.id == this.seriesId);
    if (series && preExistingIndex == -1) {
        this.chosensSeries.push(series);
    }
  }

  removeSeries(series: Series) {
    let index: number = this.chosensSeries.findIndex((value: Series) => {
        return value.id == series.id;
    });
    this.chosensSeries.splice(index, 1);
  }
}
