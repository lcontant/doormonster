import {User} from "./user";
import {Video} from "./video";

export class Comment {
  constructor(
    public commentId: number = undefined,
    public title: string = undefined,
    public userId: number = undefined,
    public author: User = undefined,
    public mediaId: string = undefined,
    public parentCommentId: number = undefined,
    public text: string = undefined,
    public replies: Comment[] = undefined,
    public showRepliesFor: boolean = undefined,
    public edited: boolean = undefined,
    public score: number = undefined,
    public createdOn: Date = undefined,
    public modifiedOn: Date = undefined
  )
  {

  }


}
