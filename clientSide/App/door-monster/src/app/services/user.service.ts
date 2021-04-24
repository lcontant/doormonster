import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ApiService} from "./api.service";
import {User} from "../model/user";
import {Observable} from "rxjs";
import {SessionService} from "./session.service";
import {Role} from "../model/role";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: "root"
})
export class UserService {

  userSubcribers: Function[];

  constructor(private httpClient: HttpClient,
              private apiService: ApiService,
              private sessionService: SessionService) {
    this.userSubcribers = [];
  }

  createUser(user: User): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/create`;
    const requestUser = {...user}; // clone User
    requestUser.avatar = null;
    return this.httpClient.post<string>(requestUrl, requestUser);
  }

  authenticateUser(user: User): Observable<any> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/authenticate`;
    return this.httpClient.post<string>(requestUrl, user);
  }

  uploadAvatar(user: User, fileName: string) {
    const requestUrl = `${this.apiService.getBaseURL()}/user/avatar`;
    const formData: FormData = new FormData();
    formData.append("file", user.avatar, user.avatar.name);
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "FileName": fileName
    });
    return this.httpClient.post<boolean>(requestUrl, formData, {headers: headers});
  }

  updateUser(user: User): Observable<User> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/update`;
    let requestUser = {...user};
    requestUser.avatar = null;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.put<User>(requestUrl, requestUser, {headers: headers});
  }

  getUser(): Observable<User> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/session`;
    const headers: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<User>(requestUrl, {headers: headers}).pipe(map((response: User) => {
      return new User(response.username
        , response.password
        , response.location
        , response.email
        , response.fullname
        , response.useFullName
        , response.avatar
        , response.userId
        , response.patreonContribution
        , response.isActivated
        , response.isBanned
        , response.isSubscribedToEmailNotifications)
    }));
  }

  getCurrentRole(): Observable<Role> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/role`;
    const headears: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<Role>(requestUrl, {headers: headears});
  }

  activateUser(activationId: string) {
    const requestUrl = `${this.apiService.getBaseURL()}/user/activation`;
    const headers: HttpHeaders = new HttpHeaders({
      "ActivationId": activationId
    });
    return this.httpClient.put<String>(requestUrl, null, {headers: headers});
  }

  sendPasswordReset(email: string): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/passwordReset`;
    const headers: HttpHeaders = new HttpHeaders({
      "Email": email
    });
    return this.httpClient.get<string>(requestUrl, {headers: headers});
  }

  resetPassword(token: string, newPassword: string): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/password`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "token": token,
      "password": newPassword
    });
    return this.httpClient.put<string>(requestUrl, null, {headers: requestHeaders});
  }

  logout() {
    const requestUrl = `${this.apiService.getBaseURL()}/user/logout`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.put(requestUrl, null, {headers: requestHeaders});
  }

  updatePassword(oldPassword: string, newPassword: string) {
    const requestUrl = `${this.apiService.getBaseURL()}/user/passwordEdit`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "oldPassword": oldPassword,
      "newPassword": newPassword
    });
    return this.httpClient.put(requestUrl, null, {headers: requestHeaders});
  }

  subscribe(callback: Function) {
    this.userSubcribers.push(callback);
  }

  resendVerificationEmail(): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/resendConfirmation`
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
    });
    return this.httpClient.put<string>(requestUrl, null, {headers: requestHeaders});
  }

  resendVerificationFromAdmin(userId: number): Observable<string> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/admin/resendConfirmation/${userId}`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<string>(requestUrl, {headers: requestHeaders});
  }

  onUserUpdate() {
    for (let callback of this.userSubcribers) {
      callback();
    }
  }

  getAllUsers() {
    const requestUrl = `${this.apiService.getBaseURL()}/user/all`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
    });
    return this.httpClient.get<User[]>(requestUrl, {headers: requestHeaders}).pipe(map((response: User[]) => {
      let users: User[] = [];
      for (const user of response) {
           users.push(this.getUserFromResponse(user));
      }
      return users as User[];
    }));
  }

  getUserById(userId: number) : Observable<User> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/${userId}`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.get<User>(requestUrl, {headers: requestHeaders}).pipe(map((response: User) => {
      return this.getUserFromResponse(response) as User;
    }));
  }

  banUserById(userId: number): Observable<boolean> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/ban/${userId}`;
    const requestHeader: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.post<boolean>(requestUrl, null, {headers: requestHeader});
  }

  unBanUserById(userId: number): Observable<boolean> {
    const requestUrl = `${this.apiService.getBaseURL()}/user/unban/${userId}`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId()
    });
    return this.httpClient.post<boolean>(requestUrl, null, {headers: requestHeaders});
  }

  linkDiscord(code: string): Observable<string> {
    let requestUrl = `${this.apiService.getBaseURL()}/user/discord/link`;
    const requestHeaders: HttpHeaders = new HttpHeaders({
      "SessionId": this.sessionService.getSessionId(),
      "code": code
    });
    return this.httpClient.put<string>(requestUrl, null, {headers: requestHeaders});
  }

  private getUserFromResponse(user: User): User {
    return new User(user.username
      , user.password
      , user.location
      , user.email
      , user.fullname
      , user.useFullName
      , user.avatar
      , user.userId
      , user.patreonContribution
      , user.isActivated
      , user.isBanned
      , user.isSubscribedToEmailNotifications)
  }
}
