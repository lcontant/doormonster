export class User {
  constructor(
    public username: string = undefined,
    public password: string = undefined,
    public location: string = undefined,
    public email: string = undefined,
    public fullname: string = undefined,
    public useFullName: boolean = undefined,
    public avatar: File = undefined,
    public userId: number = undefined,
    public patreonContribution: number = undefined,
    public isActivated: boolean = undefined,
    public isBanned: boolean = undefined,
    public isSubscribedToEmailNotifications: boolean = undefined,
  ) {
  }

  public getDisplayName(): string {
    return this.useFullName ? this.fullname : this.username;
  }
}
