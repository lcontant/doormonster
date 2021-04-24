import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PatreonRedirectComponent } from './patreon-redirect.component';

describe('PatreonRedirectComponent', () => {
  let component: PatreonRedirectComponent;
  let fixture: ComponentFixture<PatreonRedirectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PatreonRedirectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PatreonRedirectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
