import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SupporterListComponent } from './supporter-list.component';

describe('SupporterListComponent', () => {
  let component: SupporterListComponent;
  let fixture: ComponentFixture<SupporterListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SupporterListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SupporterListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
