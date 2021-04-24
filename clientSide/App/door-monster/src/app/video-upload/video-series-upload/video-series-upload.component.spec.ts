import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoSeriesUploadComponent } from './video-series-upload.component';

describe('VideoSeriesUploadComponent', () => {
  let component: VideoSeriesUploadComponent;
  let fixture: ComponentFixture<VideoSeriesUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VideoSeriesUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VideoSeriesUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
