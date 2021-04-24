import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountPasswordEditComponent } from './account-password-edit.component';

describe('AccountPasswordEditComponent', () => {
  let component: AccountPasswordEditComponent;
  let fixture: ComponentFixture<AccountPasswordEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountPasswordEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountPasswordEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
