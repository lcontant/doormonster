import { NgModule }             from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import {AboutComponent} from "./about/about.component";
import {HomeComponent} from "./home/home.component";
import {ContactComponent} from "./contact/contact.component";
import {StoreComponent} from "./store/store.component";
import {PodcastComponent} from "./podcast/podcast.component";
import {VideosComponent} from "./videos/videos.component";
import {SeriesComponent} from "./series/series.component";
import {VideoComponent} from "./video/video.component";
import {SearchResultsComponent} from "./search-results/search-results.component";
import {CreateAccountComponent} from "./account/create-account/create-account.component";
import {LoginAccountComponent} from "./account/login-account/login-account.component";
import {AccountEditComponent} from "./account/account-edit/account-edit.component";
import {PatreonPostComponent} from "./patreon-post/patreon-post.component";
import {AccountActivateComponent} from "./account/account-activate/account-activate.component";
import {AccountPasswordResetComponent} from "./account/account-password-reset/account-password-reset.component";
import {PasswordResetRequestComponent} from "./account/password-reset-request/password-reset-request.component";
import {TwitchComponent} from "./twitch/twitch.component";
import {PatreonRedirectComponent} from "./account/patreon-redirect/patreon-redirect.component";
import {CreatorComponent} from "./creator/creator.component";
import {AdminPanelComponent} from "./admin-panel/admin-panel.component";
import {AdminGard} from "./services/admin-gard.service";
import {AdminAccountEditComponent} from "./admin-account-edit/admin-account-edit.component";
import {VideoUploadComponent} from "./video-upload/video-upload.component";
import {VideoEditComponent} from "./video-edit/video-edit.component";
import {PrivacyPolicyComponent} from "./legal/privacy-policy/privacy-policy.component";
import {TermsOfServiceComponent} from "./legal/terms-of-service/terms-of-service.component";
import {SpieseatingsaladComponent} from "./spieseatingsalad/spieseatingsalad.component";
import {PodcastSeriesComponent} from "./podcast/podcast-series/podcast-series/podcast-series.component";
import {SupportComponent} from "./support/support.component";
import {AccountGard} from "./services/account-gard.service";
import {SupportEditComponent} from "./support/support-edit/support-edit.component";
import {NoSubscriptionGuard} from "./services/no-subscription.guard";
import {SubscriptionGuard} from "./services/subscription.guard";
import {SupportPaymentEditComponent} from "./support/support-payment-edit/support-payment-edit.component";
import {PodcastUploadComponent} from "./podcast-upload/podcast-upload.component";
import {ProfileComponent} from "./profile/profile.component";
import {DiscordRedirectComponent} from "./discord-redirect/discord-redirect.component";
import {SupporterListComponent} from "./supporter-list/supporter-list.component";
import {PodcastSeriesUploadComponent} from "./podcast-upload/podcast-series-upload/podcast-series-upload.component";
import {VideoSeriesUploadComponent} from "./video-upload/video-series-upload/video-series-upload.component";
import {SecretUploadComponent} from "./secret-upload/secret-upload.component";

const routes: Routes = [
  {path: "", redirectTo: "/home", pathMatch: "full"},
  {path: "about", component: AboutComponent},
  {path: "home", component: HomeComponent},
  {path: "contact", component: ContactComponent},
  {path: "store", component: StoreComponent},
  {path: "podcast", component: PodcastComponent},
  {path: "podcasts/series/:seriesId", component: PodcastSeriesComponent},
  {path: "vids", component: VideosComponent},
  {path: "series/:seriesId", component: SeriesComponent},
  {path: "videos/:id", component: VideoComponent},
  {path: "search/:query", component: SearchResultsComponent},
  {path: "account/create", component: CreateAccountComponent},
  {path: "account/login", component: LoginAccountComponent},
  {path: "account/edit", component: AccountEditComponent},
  {path: "account/passwordReset/:resetToken", component: AccountPasswordResetComponent},
  {path: "account/passwordReset", component: PasswordResetRequestComponent},
  {path: "account/activate/:activationId", component: AccountActivateComponent},
  {path: "patreon", component: PatreonPostComponent},
  {path: "live", component: TwitchComponent},
  {path: "patreon/redirect", component: PatreonRedirectComponent},
  {path: "creators", component: CreatorComponent},
  {path: "privacypolicy", component: PrivacyPolicyComponent},
  {path: "termsofservice", component: TermsOfServiceComponent},
  {path: "admin", component: AdminPanelComponent, canActivate: [AdminGard]},
  {path: "admin/edit/:userId", component: AdminAccountEditComponent, canActivate: [AdminGard]},
  {path: "upload", component: VideoUploadComponent, canActivate: [AdminGard]},
  {path: "editVideo/:videoId", component:  VideoEditComponent, canActivate: [AdminGard]},
  {path: "uploadVideoSeries", component: VideoSeriesUploadComponent, canActivate: [AdminGard]},
  {path: "uploadPodcast", component: PodcastUploadComponent, canActivate: [AdminGard]},
  {path: "uploadPodcastSeries", component: PodcastSeriesUploadComponent, canActivate: [AdminGard]},
  {path: "admin/editVideo/:videoID", component: VideoEditComponent, canActivate: [AdminGard]},
  {path: "spieseatingsalad", component: SpieseatingsaladComponent},
  {path: "checkout", component: SupportComponent, canActivate:[AccountGard]},
  {path: "checkout", component: SupportComponent, canActivate:[AccountGard, NoSubscriptionGuard]},
  {path: "admin/editVideo/:videoID", component: VideoEditComponent, canActivate: [AdminGard]},
  {path: "manage", component: SupportEditComponent, canActivate: [AccountGard, SubscriptionGuard]},
  {path: "manage/payment", component: SupportPaymentEditComponent, canActivate: [AccountGard, SubscriptionGuard]},
  {path: "discord/redirect", component: DiscordRedirectComponent},
  {path: "profile/:userId", component: ProfileComponent},
  {path: "supporters", component: SupporterListComponent},
  {path: "secret-upload", component: SecretUploadComponent, canActivate: [AdminGard]}
];


@NgModule({
  exports: [ RouterModule ],
  imports: [ RouterModule.forRoot(routes)]
})
export class AppRoutingModule {

}
