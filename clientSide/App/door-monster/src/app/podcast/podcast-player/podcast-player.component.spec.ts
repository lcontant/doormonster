import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PodcastPlayerComponent } from './podcast-player.component';

describe('PodcastPlayerComponent', () => {
  let component: PodcastPlayerComponent;
  let fixture: ComponentFixture<PodcastPlayerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PodcastPlayerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PodcastPlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
