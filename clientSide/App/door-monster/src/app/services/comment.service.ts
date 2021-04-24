import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {Comment} from "../model/comment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Vote} from "../model/vote";
import {SessionService} from "./session.service";
import {map} from "rxjs/operators";
import {type} from "os";
import {User} from "../model/user";

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private apiService: ApiService, private httpService: HttpClient, private sessionService: SessionService) { }


  private  getHeaders(): HttpHeaders {
    return new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
  }


  public createComment(comment: Comment) {
        let requestUrl = `${this.apiService.getBaseURL()}/Comments/create`;

        return this.httpService.post(requestUrl, comment, {headers: this.getHeaders()});
  }

  public updateComment(comment: Comment) {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/update/${comment.commentId}`;
    return this.httpService.put(requestUrl, comment.text,{headers: this.getHeaders()});
  }

  public deleteComment(comment: Comment) {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/delete/${comment.commentId}`;
    return this.httpService.post(requestUrl, null,{headers: this.getHeaders()});
  }

  public getByMediaId(mediaId: string) : Observable<Comment[]>{
    let requestUrl= `${this.apiService.getBaseURL()}/Comments/media/${mediaId}`;
    return this.httpService.get<Comment[]>(requestUrl).pipe(map((response: Comment[])=> {
      return this.parseCommentArrayFromData(response);
    }));
  }

  public getRepliesTo(commentId: number): Observable<Comment[]> {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/replies/${commentId}`;
    return this.httpService.get<Comment[]>(requestUrl);
  }

  public sendVote(vote: Vote) {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/vote`;
    return this.httpService.put(requestUrl,vote, {headers: this.getHeaders()});
  }

  public getCommentById(commentId: number): Observable<Comment> {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/${commentId}`;
    return this.httpService.get<Comment>(requestUrl);
  }

  public getVotesForCurrentUser() {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/userVote`;
    let requestHeader: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpService.get<Vote[]>(requestUrl, {headers: requestHeader});
  }

  public getCommentsForUser(userId: number): Observable<Comment[]> {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/user/${userId}`;
    return this.httpService.get<Comment[]>(requestUrl).pipe(map((response: Comment[]) => {
      return this.parseCommentArrayFromData(response);
    }));
  }

  public getTopCommentsOfTheWeek(): Observable<Comment[]> {
    let requestUrl = `${this.apiService.getBaseURL()}/Comments/top`;
    return this.httpService.get<Comment[]>(requestUrl);
  }
  private parseCommentArrayFromData(commentData: Comment[]): Comment[] {
    let comments: Comment[] = [];
    for (const comment of commentData as Comment[]) {
      let currentComment = this.parceCommentFromData(comment);
      if (currentComment.replies.length > 0) {
        currentComment.replies = this.parseCommentArrayFromData(currentComment.replies);
      }
      comments.push(currentComment);
    }
    return comments as Comment[];
  }
  private parceCommentFromData(comment: Comment) {
    let author = new User(comment.author.username
      , comment.author.password
      , comment.author.location
      , comment.author.email
      , comment.author.fullname
      , comment.author.useFullName
      , comment.author.avatar
      , comment.author.userId
      , comment.author.patreonContribution
      , comment.author.isActivated
      , comment.author.isBanned
      , comment.author.isSubscribedToEmailNotifications);
    let currentComment = new Comment(comment.commentId
      , comment.title
      , comment.userId
      , author
      , comment.mediaId
      , comment.parentCommentId
      , comment.text
      , comment.replies
      , comment.showRepliesFor
      , comment.edited
      , comment.score
      , comment.createdOn
      , comment.modifiedOn);
    return currentComment;
  }
}
