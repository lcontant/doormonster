import { Component } from '@angular/core';
import { LoadingService } from './services/loading.service';
declare var toggleSideBar: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  sidebarToggled: boolean;

  constructor(public loadingService: LoadingService) {
      
  }

  onOutSideSideBarClick() {
    if (this.sidebarToggled) {
      toggleSideBar(this.sidebarToggled);
      this.sidebarToggled = !this.sidebarToggled;
    }
  }
}
