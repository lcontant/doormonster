import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  searchString: string;
  mobileSearchBarToggled: boolean;

  @Output()
  public onMobileSearchButtonClick: EventEmitter<boolean> = new EventEmitter();

  constructor(private route: ActivatedRoute
    , private router: Router) {
  }

  ngOnInit() {
    this.searchString = this.route.snapshot.paramMap.get('query');
    this.mobileSearchBarToggled = false;
  }

  search() {

  }

  onSubmit(event) {
    if (!this.searchString || this.searchString.length < 0) {
      event.preventDefault();
    } else {
      this.router.navigateByUrl(`/search/${this.searchString}`);
    }
  }

  toggleSearchBarExpension() {
    this.mobileSearchBarToggled = !this.mobileSearchBarToggled;
    this.onMobileSearchButtonClick.emit(this.mobileSearchBarToggled);
  }

}
