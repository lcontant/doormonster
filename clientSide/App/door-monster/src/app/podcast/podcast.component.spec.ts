import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PodcastComponent } from './podcast.component';

describe('PodcastComponent', () => {
  let component: PodcastComponent;
  let fixture: ComponentFixture<PodcastComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PodcastComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PodcastComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should edit', () => {
    expect(component).toBeTruthy();
  });
});
