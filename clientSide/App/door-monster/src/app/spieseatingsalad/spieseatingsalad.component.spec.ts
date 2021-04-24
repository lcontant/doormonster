import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpieseatingsaladComponent } from './spieseatingsalad.component';

describe('SpieseatingsaladComponent', () => {
  let component: SpieseatingsaladComponent;
  let fixture: ComponentFixture<SpieseatingsaladComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SpieseatingsaladComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpieseatingsaladComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
