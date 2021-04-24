import {Component, OnInit} from '@angular/core';
import {Series} from "../../model/series";
import {VideoService} from "../../services/video.service";
import {ImageService} from "../../services/image.service";

@Component({
  selector: 'app-video-series-upload',
  templateUrl: './video-series-upload.component.html',
  styleUrls: ['./video-series-upload.component.css']
})
export class VideoSeriesUploadComponent implements OnInit {
  series: Series;
  thumbnailFile: File;
  currentThumbnail: string;
  errorMessage: string;
  successMessage: string;
  progressMessage: string;
  uploadProgress: number;

  constructor(private videoService: VideoService, private imageService: ImageService) {
  }

  ngOnInit() {
    this.uploadProgress = 0;
    this.series = new Series();
  }

  onThumbnailChanged($event) {
    this.thumbnailFile = $event.target.files[0];
    this.thumbnailFile.name;
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
  uploadVideoSeries() {
    this.series.textId = this.series.title.split(" ").join("").toLowerCase();
    this.uploadProgress = 0;
    this.progressMessage = "";
    this.errorMessage = "";
    this.successMessage = "";
    this.videoService.createSeries(this.series).subscribe(response => {
      this.progressMessage = "Metadata uploaded, uploading thumbnail";
      this.uploadProgress = 20;
      this.videoService.uploadSeriesThumbnail(this.thumbnailFile, this.thumbnailFile.name, this.series.textId).subscribe(response => {
          this.progressMessage = "";
          this.uploadProgress = 100;
          this.successMessage = "Done uploading the series and the thumbnail";
      }, error => {
        this.progressMessage = "";
        this.errorMessage = "Error while uploading the thumbnail"
      });
    }, error => {
      this.progressMessage = "";
      this.errorMessage = "Error while uploading the series metadata"
    })
  }
}
