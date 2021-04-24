import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {CommentService} from "../services/comment.service";
import {Comment} from "../model/comment";
import {UserService} from "../services/user.service";
import {User} from "../model/user";
import {ActivatedRoute, Router} from "@angular/router";
import {Vote} from "../model/vote";
import {Role} from "../model/role";

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {

  @Input("mediaId") mediaId: string;
  @ViewChild("accountModal") accountModal: any;
  @Input("userId") userId: number;
  @Input("isComponent") isComponent: boolean;

  displayComments: Comment[];
  comments: Comment[];
  votes: Vote[] = [];
  showingRepliesFor: number[];
  currentEditingId: number;
  currentUser: User;
  currentRole: Role;
  currentCommentReply: Comment;
  inputComment: string;
  replyingTo: User;
  isCommenting: boolean;
  isReplying: boolean;
  sortedByUpVote: boolean;
  leaveACommentMessage: string;

  constructor(private commentService: CommentService
    , private userService: UserService
    , private route: ActivatedRoute
    , private router: Router) {

  }

  ngOnInit() {
    this.isCommenting = false;
    this.sortedByUpVote = false;
    this.showingRepliesFor = [];
    this.displayComments = [];
    this.comments = [];
    this.inputComment = "";
    this.currentUser = null;
    this.isReplying = false;
    this.currentEditingId = -1;
    this.votes = [];
    this.setupData();
  }

  private setupData() {
    if (this.userId) {
      this.getCommentsForUser();
      this.route.params.subscribe(params => {
        this.userId = params.userId;
        this.getCommentsForUser();
        this.getCurrentUser();
      });
    } else {
      this.getComments();
      this.route.params.subscribe(params => {
        this.mediaId = !this.isComponent ? params.id : this.mediaId;
        this.getComments();
        this.getCurrentUser();
      });
    }
    this.getCurrentUser();
    this.userService.getCurrentRole().subscribe(role => {
      this.currentRole = role;
    });
  }

  showMoreComments() {
    this.displayComments = this.comments;
  }

  sortByUpvote() {
    this.displayComments.sort((comment1, comment2) => comment2.score - comment1.score);
    this.sortedByUpVote = true;
  }

  sortByDate() {
    this.displayComments.sort((commentA, commentB) => commentB.createdOn.getTime() - commentA.createdOn.getTime());
    this.sortedByUpVote = false;
  }

  isEditing(commentId: number) {
    return this.currentEditingId == commentId;
  }

  startEditing(commentId: number) {
    if (this.isEditing(commentId)) {
      this.cancelUpdate();
    } else {
      this.currentEditingId = commentId;
    }
  }

  deleteComment(comment: Comment) {
    this.commentService.deleteComment(comment).subscribe(response => {
      this.getComments();
    });
  }

  cancelUpdate() {
    this.currentEditingId = -1;
    this.getComments();
  }

  updateComment(comment: Comment) {
    this.commentService.updateComment(comment).subscribe(response => {
      this.currentEditingId = -1;
      this.getComments();
    }, error => {
      this.getComments();
    });
  }

  GoToCreateAnAccount() {
    this.router.navigateByUrl("/account/create");
  }

  getVotesForCurrentUser() {
    this.commentService.getVotesForCurrentUser().subscribe(votes => {
      this.votes = votes;
    });
  }

  hasUpvoted(commentId: number) {
    let relevantVote = this.votes.filter((vote: Vote) => vote.commentId == commentId && vote.isUpVote);
    return relevantVote.length > 0;
  }

  hasDownVoted(commentId: number) {
    let relevantVote = this.votes.filter((vote: Vote) => vote.commentId == commentId && !vote.isUpVote);
    return relevantVote.length > 0;
  }

  getCurrentUser() {
    this.userService.getUser().subscribe(response => {
        this.currentUser = response;
        this.getVotesForCurrentUser();
      },
      error => {

      })
  }

  getCommentsForUser() {
    this.commentService.getCommentsForUser(Number(this.userId)).subscribe(response => {
      this.comments = response;
      console.log(this.comments.constructor.name);
      for (let comment of this.comments) {
        comment.createdOn = new Date(String(comment.createdOn));
        comment.modifiedOn = new Date(String(comment.modifiedOn));
        comment.edited = comment.createdOn.getTime() != comment.modifiedOn.getTime();
        for (let reply of comment.replies) {
          reply.createdOn = new Date(String(reply.createdOn));
          reply.modifiedOn = new Date(String(reply.modifiedOn));
          reply.edited = reply.createdOn.getTime() != reply.modifiedOn.getTime();
        }
        if (this.showingRepliesFor.indexOf(comment.commentId) != -1) {
          comment.showRepliesFor = true;
        }
      }
      this.displayComments = this.comments.slice(0, 10);
      if (this.sortedByUpVote) {
        this.sortByUpvote();
      }
    }, error1 => {

    });
  }

  getComments() {
    this.commentService.getByMediaId(this.mediaId).subscribe((response: Comment[]) => {
      this.comments = response as Comment[];
      for (let comment of this.comments) {
        comment.createdOn = new Date(String(comment.createdOn));
        comment.modifiedOn = new Date(String(comment.modifiedOn));
        comment.edited = comment.createdOn.getTime() != comment.modifiedOn.getTime();
        comment.author = comment.author as User;
        for (let reply of comment.replies) {
          reply.createdOn = new Date(String(reply.createdOn));
          reply.modifiedOn = new Date(String(reply.modifiedOn));
          reply.edited = reply.createdOn.getTime() != reply.modifiedOn.getTime();
        }
        if (this.showingRepliesFor.indexOf(comment.commentId) != -1) {
          comment.showRepliesFor = true;
        }
      }
      this.displayComments = this.comments.slice(0, 10);
      if (this.sortedByUpVote) {
        this.sortByUpvote();
      }
    });
  }

  enterComment() {
    if (this.currentUser && this.currentUser.isActivated) {
      this.isCommenting = true;
    } else {
      this.showModal();
    }
  }

  cancelCommentIntput() {
    this.isCommenting = false;
  }


  startReplyingTo(comment: Comment) {
    if (this.currentUser) {
      if (!this.isReplyingTo(comment)) {
        this.currentCommentReply = comment;
        comment.showRepliesFor = true;
        this.isReplying = true;
        this.replyingTo = comment.author;
      } else {
        this.currentCommentReply = null;
        this.isReplying = false;
      }
    } else {
      this.showModal();
    }
  }

  isReplyingTo(comment: Comment) {
    return this.currentCommentReply == comment;
  }

  vote(commentId: number, isUpVote: boolean) {
    if (this.currentUser) {
      let vote: Vote = new Vote();
      vote.userId = this.currentUser.userId;
      vote.commentId = commentId;
      vote.isUpVote = isUpVote;
      this.commentService.sendVote(vote).subscribe(response => {
        this.getVotesForCurrentUser();
        //TODO: This whole search is unnecessary, switch to just sending the comment via param
        let concernedComment = this.comments.find((comment) => comment.commentId == commentId);
        if (concernedComment == undefined) {
          for (let comment of this.comments) {
            if (comment.replies) {
              for (let reply of comment.replies) {
                if (reply.commentId == commentId) {
                  concernedComment = reply;
                  break;
                }
              }
            }
          }
        }
        this.commentService.getCommentById(concernedComment.commentId).subscribe(response => {
          concernedComment.score = response.score;
        });
      })
    } else {
      this.showModal();
    }
  }

  getRepliesFor(commentId: number) {
    let pickedComment = this.comments.find((comment) => comment.commentId == commentId);
    if (!pickedComment.showRepliesFor) {
      pickedComment.showRepliesFor = true;
      this.showingRepliesFor.push(pickedComment.commentId)
    } else {
      pickedComment.showRepliesFor = false;
      let indexOfComment = this.showingRepliesFor.indexOf(pickedComment.commentId);
      this.showingRepliesFor.splice(indexOfComment, 1);
    }
  }

  closeModal() {
    this.accountModal.nativeElement.classList.remove("is-active");
  }

  private showModal() {
    this.accountModal.nativeElement.classList.add("is-active");
  }


}
