import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PodcastSeriesComponent } from './podcast-series.component';

describe('PodcastSeriesComponent', () => {
  let component: PodcastSeriesComponent;
  let fixture: ComponentFixture<PodcastSeriesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PodcastSeriesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PodcastSeriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
