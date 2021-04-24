import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAccountEditComponent } from './admin-account-edit.component';

describe('AdminAccountEditComponent', () => {
  let component: AdminAccountEditComponent;
  let fixture: ComponentFixture<AdminAccountEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminAccountEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminAccountEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
