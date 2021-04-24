import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PodcastUploadComponent } from './podcast-upload.component';

describe('PodcastUploadComponent', () => {
  let component: PodcastUploadComponent;
  let fixture: ComponentFixture<PodcastUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PodcastUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PodcastUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
