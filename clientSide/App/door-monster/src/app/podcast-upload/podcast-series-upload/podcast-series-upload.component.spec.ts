import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PodcastSeriesUploadComponent } from './podcast-series-upload.component';

describe('PodcastSeriesUploadComponent', () => {
  let component: PodcastSeriesUploadComponent;
  let fixture: ComponentFixture<PodcastSeriesUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PodcastSeriesUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PodcastSeriesUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
