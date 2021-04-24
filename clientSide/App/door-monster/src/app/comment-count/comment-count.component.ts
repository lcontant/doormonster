import {Component, Input, OnInit} from "@angular/core";
import {Video} from "../model/video";
import {DoorMonsterDisqusService} from "../services/door-monster-disqus.service";

@Component({
  selector: "app-comment-count",
  templateUrl: "./comment-count.component.html",
  styleUrls: ["./comment-count.component.css"]
})
export class CommentCountComponent implements OnInit {

  @Input() video: Video;

  constructor(private disqusService: DoorMonsterDisqusService) { }

  ngOnInit() {
    this.disqusService.getCommentCountForVideo(this.video).subscribe(response => {
    });
  }

}
