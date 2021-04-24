import { Component, OnInit } from "@angular/core";
import {User} from "../model/user";
import {UserService} from "../services/user.service";
import {Video} from "../model/video";
import {VideoService} from "../services/video.service";
import {FeedbackService} from "../services/feedback.service";
import {FeedbackWithUser} from "../model/feedback-with-user";
import {SupporterWithUser} from "../model/supporter-with-user";
import {SupportService} from "../services/support.service";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: "app-admin-panel",
  templateUrl: "./admin-panel.component.html",
  styleUrls: ["./admin-panel.component.css"]
})
export class AdminPanelComponent implements OnInit {
  users: User[];
  users_list: User[];
  videos: Video[];
  searchString: string;
  supporterUsers: SupporterWithUser[];
  userSearchString: string;
  activeTabName: string;
  totalAmount: number;
  feedbackItems: FeedbackWithUser[];

  constructor(private userService: UserService
              , private videoService: VideoService
              , private feedbackService: FeedbackService
              , private supportService: SupportService
              , private notificationService: NotificationService) { }

  ngOnInit() {
    this.activeTabName = "Users";
    this.totalAmount = 0;
    this.userService.getAllUsers().subscribe(users => {
      this.users = users;
      this.users_list = this.users.slice(0, 10);
    });
    this.videoService.getAllVideos().subscribe(videos => {
      this.videos = videos;
    });
    this.feedbackService.getGetAllFeedback().subscribe(feedbackItems => {
      this.feedbackItems = feedbackItems;
      for (const item of feedbackItems) {
        item.feedback.content = item.feedback.content.split("\"").join("").split("\\n").join("\n");
      }
    });
    this.supportService.list().subscribe(response => {
      this.supporterUsers = response;
      this.totalAmount = 0;
      for (const supporter of this.supporterUsers) {
        this.totalAmount += supporter.supporter.ammount / 100;
      }
    }, error => {
      this.notificationService.createErrorNotification("Couldn't load supporters");
    });
  }

  setActiveTab(tabName: string) {
    this.activeTabName = tabName;
  }

  isActiveTab(tabName: string) {
    return this.activeTabName == tabName;
  }

  searchUser(searchQuery: string) {
    if (searchQuery && searchQuery.length > 0) {
      this.users_list = this.users.filter((user => user.username.indexOf(searchQuery) != -1));
    } else {
      this.users_list = this.users.slice(0, 10);
    }
  }

  search() {
    this.videoService.getVideosForQuery(this.searchString).subscribe(videos => {
        this.videos = videos;
    });
  }

}
