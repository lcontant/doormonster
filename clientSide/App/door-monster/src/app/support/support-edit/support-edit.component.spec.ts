import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportEditComponent } from './support-edit.component';

describe('SupportEditComponent', () => {
  let component: SupportEditComponent;
  let fixture: ComponentFixture<SupportEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SupportEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SupportEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
