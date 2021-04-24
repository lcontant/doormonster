import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {RouterModule, Routes} from "@angular/router";

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { SearchComponent } from './search/search.component';
import { AboutComponent } from './about/about.component';
import {AppRoutingModule} from "./app-routing.module";
import { FooterComponent } from './footer/footer.component';
import {HttpClient, HttpClientModule} from "@angular/common/http";
import { HomeComponent } from './home/home.component';
import { ContactComponent } from './contact/contact.component';
import { StoreComponent } from './store/store.component';
import { PodcastComponent } from './podcast/podcast.component';
import { VideosComponent } from './videos/videos.component';
import { SeriesComponent } from './series/series.component';
import { VideoComponent } from './video/video.component';
import { SearchResultsComponent } from './search-results/search-results.component';
import {FormsModule} from "@angular/forms";
import { CommentCountComponent } from './comment-count/comment-count.component';
import { CreateAccountComponent } from './account/create-account/create-account.component';
import { LoginAccountComponent } from './account/login-account/login-account.component';
import { AccountEditComponent } from './account/account-edit/account-edit.component';
import { CreatorComponent } from './creator/creator.component';
import { ExampleComponent } from './example/example.component';
import { PatreonPostComponent } from './patreon-post/patreon-post.component';
import { AccountActivateComponent } from './account/account-activate/account-activate.component';
import {constants} from "./model/constants";
import { AccountPasswordResetComponent } from './account/account-password-reset/account-password-reset.component';
import { PasswordResetRequestComponent } from './account/password-reset-request/password-reset-request.component';
import {CookieService} from "ngx-cookie-service";
import { CommentsComponent } from './comments/comments.component';
import { CommentInputComponent } from './comments/comment-input/comment-input.component';

import { environment } from '../environments/environment';
import { VideoPlayerComponent } from './video/video-player/video-player.component';
import {PatreonRedirectComponent} from "./account/patreon-redirect/patreon-redirect.component";
import { TwitchComponent } from './twitch/twitch.component';
import { MobileNavComponent } from './mobile-nav/mobile-nav.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { AdminPanelComponent } from './admin-panel/admin-panel.component';
import { AdminAccountEditComponent } from './admin-account-edit/admin-account-edit.component';
import { VideoUploadComponent } from './video-upload/video-upload.component';
import { VideoEditComponent } from './video-edit/video-edit.component';
import { TermsOfServiceComponent } from './legal/terms-of-service/terms-of-service.component';
import { PrivacyPolicyComponent } from './legal/privacy-policy/privacy-policy.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { LoadingComponent } from './loading/loading.component';
import { SupportComponent } from './support/support.component';
import { NotificationComponent } from './notification/notification.component';
import { PodcastPlayerComponent } from './podcast/podcast-player/podcast-player.component';
import { PodcastSeriesComponent } from './podcast/podcast-series/podcast-series/podcast-series.component';
import { SpieseatingsaladComponent } from './spieseatingsalad/spieseatingsalad.component';
import { SupportEditComponent } from './support/support-edit/support-edit.component';
import { SupportPaymentEditComponent } from './support/support-payment-edit/support-payment-edit.component';
import { PodcastUploadComponent } from './podcast-upload/podcast-upload.component';
import { ProfileComponent } from './profile/profile.component';
import { DiscordRedirectComponent } from './discord-redirect/discord-redirect.component';
import {HelpComponent} from "./help/help.component";
import { AccountActivationComponent } from './account/account-activation/account-activation.component';
import { AccountInfoEditComponent } from './account/account-info-edit/account-info-edit.component';
import { AccountPasswordEditComponent } from './account/account-password-edit/account-password-edit.component';
import { SupporterListComponent } from './supporter-list/supporter-list.component';
import { PodcastSeriesUploadComponent } from './podcast-upload/podcast-series-upload/podcast-series-upload.component';
import { VideoSeriesUploadComponent } from './video-upload/video-series-upload/video-series-upload.component';
import { SecretUploadComponent } from './secret-upload/secret-upload.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SearchComponent,
    AboutComponent,
    FooterComponent,
    HomeComponent,
    ContactComponent,
    StoreComponent,
    PodcastComponent,
    VideosComponent,
    SeriesComponent,
    VideoComponent,
    SearchResultsComponent,
    CommentCountComponent,
    CreateAccountComponent,
    LoginAccountComponent,
    AccountEditComponent,
    CreatorComponent,
    ExampleComponent,
    PatreonPostComponent,
    AccountActivateComponent,
    AccountPasswordResetComponent,
    PasswordResetRequestComponent,
    CommentsComponent,
    CommentInputComponent,
    VideoPlayerComponent,
    PatreonRedirectComponent,
    TwitchComponent,
    MobileNavComponent,
    SidebarComponent,
    AdminPanelComponent,
    AdminAccountEditComponent,
    VideoUploadComponent,
    VideoEditComponent,
    TermsOfServiceComponent,
    PrivacyPolicyComponent,
    LoadingComponent,
    NotificationComponent,
    PodcastPlayerComponent,
    SpieseatingsaladComponent,
    PodcastSeriesComponent,
    SupportComponent,
    SupportEditComponent,
    SupportPaymentEditComponent,
    PodcastUploadComponent,
    DiscordRedirectComponent,
    HelpComponent,
    ProfileComponent,
    AccountActivationComponent,
    AccountInfoEditComponent,
    AccountPasswordEditComponent,
    SupporterListComponent,
    PodcastSeriesUploadComponent,
    VideoSeriesUploadComponent,
    SecretUploadComponent,
  ],
  imports: [
    BrowserModule,
    RouterModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production })
  ],
  providers: [
    constants,
    CookieService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
