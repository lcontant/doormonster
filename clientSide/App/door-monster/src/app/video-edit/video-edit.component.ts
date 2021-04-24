import {Component, OnInit} from '@angular/core';
import {Video} from "../model/video";
import {VideoService} from "../services/video.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ImageService} from "../services/image.service";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-video-edit',
  templateUrl: './video-edit.component.html',
  styleUrls: ['./video-edit.component.css']
})
export class VideoEditComponent implements OnInit {

  thumbnailFile: File;
  videoFile: File;
  currentThumbnail: string;
  currentDate: string;
  videoId: string;
  video: Video = new Video();
  seriesNames: string[];
  categoriesNames: string[];
  prePath: string;
  errorMessage: string;
  successMessage: string;
  progressMessage: string;
  uploadProgress: number;

  constructor(private videoService: VideoService, private route: ActivatedRoute, private router: Router, private imageService: ImageService) {

  }

  ngOnInit() {
    this.currentDate = "";
    this.videoId = this.route.snapshot.paramMap.get("videoId");
    this.videoService.getByVideoId(this.videoId).subscribe((video: Video) => {
      let datePipe: DatePipe = new DatePipe("en-US");
      this.video = video;
     this.video.videoPublishDate  = datePipe.transform(new Date(Date.parse(video.videoPublishDate.toString())),"yyyy-MM-dd");
      this.currentDate = this.video.videoPublishDate;
      console.log(this.video.videoPublishDate);
    });
    this.videoService.getSeriesNames().subscribe(names => {
      this.seriesNames = names;
    });
    this.videoService.getCategoriesNames().subscribe(names => {
      this.categoriesNames = names;
    });
  }

  onDateChange(stringDate: string) {
    this.video.videoPublishDate = stringDate + "T00:00:00";
  }

  edit() {
    let datePipe: DatePipe = new DatePipe("en-US");
    this.video.videoPublishDate = datePipe.transform(new Date(Date.parse(this.video.videoPublishDate.toString())), "yyyy-MM-dd") + "T00:00:00";
    if (this.video.videoThumbnail.indexOf(this.prePath) == -1) {
       this.video.videoThumbnail = this.prePath + "/" +  this.video.videoThumbnail;
    }
    this.successMessage = "";
    this.errorMessage = "";
    this.progressMessage = "Starting to upload the video";
    this.uploadProgress = 10;
    this.videoService.updateVideo(this.video).subscribe(success => {
      this.progressMessage = "Uploading the thumbnail";
      this.uploadProgress += 20;
      if (this.thumbnailFile) {
        this.videoService.uploadThumbnail(this.thumbnailFile, this.video.videoThumbnail, this.prePath).subscribe(response => {
          this.uploadProgress += 20;
        }, error => {
          this.errorMessage = "There was a problem uploading the thumbnail send it to Louis on discord"
        });
      } else {
        this.uploadProgress += 20;
      }
      if (this.videoFile) {
          this.progressMessage = "Uploading the video file";
          this.uploadProgress += 5;
          this.videoService.uploadVideoFile(this.videoFile, this.video.videoFileLink).subscribe(response => {
              this.uploadProgress = 100;
              this.successMessage = "Finished uploading the video";
          }, error => {
            this.errorMessage = "There was a problem uploading the video file tell Louis and send him a link to the file;";
          });
        } else {
          this.uploadProgress = 100;
        this.successMessage = "Finished uploading the video";
      }
      }, error =>  {
        this.errorMessage = "There was a problem uploading the video metadata (Title, description and such)"
    });

  }

  delete() {
    this.videoService.deleteVideo(this.video.id).subscribe(success => {
      }
      , error => {

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

}
