import {Comment} from "../../model/comment";
import {CommentService} from "../../services/comment.service";
import {User} from "../../model/user";
import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";

@Component({
  selector: 'app-comment-input',
  templateUrl: './comment-input.component.html',
  styleUrls: ['./comment-input.component.css']
})
export class CommentInputComponent implements OnInit {


  @Input() user: User;
  @Input() mediaId: string;
  @Input() parentCommentId: number;
  @Input() isCommenting: boolean;
  @Input() isReplying: boolean;
  @Input() replyingTo: User;

  @Output() onCommentSubmit: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() isCommentingChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  inputComment: string;
  constructor(private commentService: CommentService) { }

  ngOnInit() {
    this.inputComment = "";
    if (this.isReplying) {
          this.inputComment = "@" + this.replyingTo.getDisplayName();
    }
    this.isCommenting = false;
  }

  sendComment() {
    let comment = new Comment();
    comment.text = this.inputComment;
    comment.userId = this.user.userId;
    comment.mediaId = this.mediaId;
    comment.parentCommentId = this.parentCommentId;
    this.commentService.createComment(comment).subscribe(response => {
        this.onCommentSubmit.emit(true);
        this.isCommentingChange.emit(false);
      },
      error => {

      });
  }

  canSubmitComment() {
    return this.inputComment.length > 0;
  }

  cancel() {
    this.isCommenting = false;
    this.isCommentingChange.emit(this.isCommenting);
  }

}
