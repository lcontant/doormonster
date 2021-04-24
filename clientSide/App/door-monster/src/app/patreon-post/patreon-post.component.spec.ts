import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PatreonPostComponent } from './patreon-post.component';

describe('PatreonPostComponent', () => {
  let component: PatreonPostComponent;
  let fixture: ComponentFixture<PatreonPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PatreonPostComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PatreonPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
